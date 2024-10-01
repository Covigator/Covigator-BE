package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.exception.badrequest.DuplicateMemberException;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import com.ku.covigator.security.kakao.KakaoOauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final KakaoOauthProvider kakaoOauthProvider;
    private final S3Service s3Service;

    private static final String BASE_NICKNAME = "코비게이터";
    private static final int MAX_UID = 99999;

    @Transactional(readOnly = true)
    public String signIn(String email, String password) {
        Member member = memberRepository.findByEmailAndPlatform(email, Platform.LOCAL)
                .orElseThrow(NotFoundMemberException::new);
        validatePassword(password, member.getPassword());
        return jwtProvider.createToken(member.getId().toString());
    }

    // 로컬 회원가입
    public String signUp(Member member, MultipartFile image) {

        // 닉네임 중복 검증
        validateNicknameDuplication(member.getNickname());

        // 이메일 중복 검증
        validateEmailDuplication(member.getEmail());

        // 패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.encodePassword(encodedPassword);

        // S3에 프로필 이미지 업로드
        if (image != null && !image.isEmpty()) {
            String uploadedImageUrl = s3Service.uploadImage(image, "profile");
            member.addImageUrl(uploadedImageUrl);
        }

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        // 토큰 반환
        return jwtProvider.createToken(savedMember.getId().toString());
    }

    // 카카오 회원가입
    public KakaoSignInResponse signInKakao(String code) {

        // 카카오 토큰 반환
        KakaoTokenResponse kakaoTokenResponse = kakaoOauthProvider.getKakaoToken(code);

        // 카카오 사용자 정보 반환
        KakaoUserInfoResponse kakaoUserInfoResponse = kakaoOauthProvider.getKakaoUserInfo(kakaoTokenResponse.accessToken());

        // 회원 가입 여부 확인
        String email = kakaoUserInfoResponse.kakaoAccount().email();
        Optional<Member> savedMember = memberRepository.findByEmailAndPlatform(email, Platform.KAKAO);

        // 가입된 회원 반환
        if (savedMember.isPresent()) {
            String token = jwtProvider.createToken(savedMember.get().getId().toString());
            return KakaoSignInResponse.fromOldMember(token);
        }

        // 신규 닉네임 생성
        String nickname = createRandomNickname();
        Member member = kakaoUserInfoResponse.toEntity();
        member.updateNickname(nickname);

        // 신규 회원 저장
        Member newMember = memberRepository.save(member);
        String token = jwtProvider.createToken(newMember.getId().toString());
        return KakaoSignInResponse.fromNewMember(token);

    }

    // 닉네임 중복 검증
    private void validateNicknameDuplication(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);
        if (member.isPresent()) {
            throw new DuplicateMemberNicknameException();
        }
    }

    // 이메일 중복 검증 (로컬)
    private void validateEmailDuplication(String email) {
        Optional<Member> member = memberRepository.findByEmailAndPlatform(email, Platform.LOCAL);
        if (member.isPresent()) {
            throw new DuplicateMemberException();
        }
    }

    // 비밀 번호 검증
    private void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new PasswordMismatchException();
        }
    }

    // 신규 닉네임 생성
    private String createRandomNickname() {
        SecureRandom secureRandom = new SecureRandom();
        String nickname;
        do {
            nickname = BASE_NICKNAME + (secureRandom.nextInt(MAX_UID) + 1);
        } while (isNicknameDuplicated(nickname));
        return nickname;
    }

    // 닉네임 중복 여부 확인
    private Boolean isNicknameDuplicated(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

}
