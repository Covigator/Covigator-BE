package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.exception.badrequest.DuplicateMemberException;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundEmailException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import com.ku.covigator.security.kakao.KakaoOauthProvider;
import com.ku.covigator.support.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.ku.covigator.common.EmailVerificationTemplate.*;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final KakaoOauthProvider kakaoOauthProvider;
    private final S3Service s3Service;
    private final SESService sesService;
    private final RedisUtil redisUtil;

    private static final String BASE_NICKNAME = "코비게이터";
    private static final int MAX_UID = 99999;
    private static final int VERIFICATION_LENGTH = 8;

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
        member.savePassword(encodedPassword);

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

    // 인증번호 생성
    public void createVerificationNumber(String email) {

        // 등록된 이메일인지 확인
        memberRepository.findByEmailAndPlatform(email, Platform.LOCAL)
                .orElseThrow(NotFoundEmailException::new);

        // 인증번호 생성
        String verificationNumber = RandomUtil.generateRandomMixStr(VERIFICATION_LENGTH);

        // 이메일 전송
        sesService.sendEmail(SUBJECT.getText(),
                CONTENT_PREFIX.getText() + verificationNumber + CONTENT_SUFFIX.getText(),
                email);

        // Redis에 인증번호 저장
        redisUtil.setDataExpire(email, verificationNumber, 60 * 5L);

    }

    // 비밀번호 변경
    public void changePassword(Long memberId, String password) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        String encodedPassword = passwordEncoder.encode(password);
        member.savePassword(encodedPassword);

        memberRepository.save(member);
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
        String nickname;
        do {
            nickname = BASE_NICKNAME + RandomUtil.generateRandomNumber(MAX_UID);
        } while (isNicknameDuplicated(nickname));
        return nickname;
    }

    // 닉네임 중복 여부 확인
    private boolean isNicknameDuplicated(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

}
