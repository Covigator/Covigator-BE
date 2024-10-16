package com.ku.covigator.service;

import com.ku.covigator.common.SmsVerificationTemplate;
import com.ku.covigator.config.properties.RtrProperties;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.dto.response.TokenResponse;
import com.ku.covigator.exception.badrequest.DuplicateMemberException;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.badrequest.InvalidRefreshTokenException;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundEmailException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import com.ku.covigator.security.kakao.KakaoOauthProvider;
import com.ku.covigator.support.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static com.ku.covigator.common.EmailVerificationTemplate.*;


@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RtrProperties.class)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final KakaoOauthProvider kakaoOauthProvider;
    private final S3Service s3Service;
    private final SESService sesService;
    private final SmsService smsService;
    private final RedisUtil redisUtil;
    private final RtrProperties rtrProperties;

    private static final String BASE_NICKNAME = "코비게이터";
    private static final int MAX_UID = 99999;
    private static final int VERIFICATION_LENGTH = 8;

    @Transactional(readOnly = true)
    public TokenResponse signIn(String email, String password) {

        Member member = memberRepository.findByEmailAndPlatform(email, Platform.LOCAL)
                .orElseThrow(NotFoundMemberException::new);

        validatePassword(password, member.getPassword());

        String accessToken = jwtProvider.createToken(member.getId().toString());

        // RTR
        String refreshToken = createRefreshToken();
        redisUtil.setDataExpire(refreshToken, String.valueOf(member.getId()), rtrProperties.getExpirationLength());

        return TokenResponse.from(accessToken, refreshToken);
    }

    // 로컬 회원가입
    public TokenResponse signUp(Member member, MultipartFile image) {

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
        String accessToken = jwtProvider.createToken(savedMember.getId().toString());
        String refreshToken = createRefreshToken();
        redisUtil.setDataExpire(refreshToken, String.valueOf(member.getId()), rtrProperties.getExpirationLength());

        return TokenResponse.from(accessToken, refreshToken);
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
            String accessToken = jwtProvider.createToken(savedMember.get().getId().toString());
            String refreshToken = createRefreshToken();
            redisUtil.setDataExpire(refreshToken, String.valueOf(savedMember.get().getId()), rtrProperties.getExpirationLength());
            return KakaoSignInResponse.fromOldMember(accessToken, refreshToken);
        }

        // 신규 닉네임 생성
        String nickname = createRandomNickname();
        Member member = kakaoUserInfoResponse.toEntity();
        member.updateNickname(nickname);

        // 신규 회원 저장
        Member newMember = memberRepository.save(member);
        String accessToken = jwtProvider.createToken(newMember.getId().toString());
        String refreshToken = createRefreshToken();
        redisUtil.setDataExpire(refreshToken, String.valueOf(newMember.getId()), rtrProperties.getExpirationLength());
        return KakaoSignInResponse.fromNewMember(accessToken, refreshToken);

    }

    // 인증번호 생성
    public void createVerificationNumber(String email) {

        // 등록된 이메일인지 확인
        Member member = memberRepository.findByEmailAndPlatform(email, Platform.LOCAL)
                .orElseThrow(NotFoundEmailException::new);

        // 인증번호 생성
        String verificationNumber = RandomUtil.generateRandomMixStr(VERIFICATION_LENGTH);

        // 이메일 전송
        sesService.sendEmail(SUBJECT.getText(),
                CONTENT_PREFIX.getText() + verificationNumber + CONTENT_SUFFIX.getText(),
                member.getEmail());

        // Redis에 인증번호 저장
        redisUtil.setDataExpire(member.getEmail(), verificationNumber, 60 * 5L);

    }

    //문자 메시지 전송 (인증번호)
    public void sendMessage(Long memberId, String phoneNumber) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        // 인증번호 생성
        String verificationNumber = RandomUtil.generateRandomMixStr(VERIFICATION_LENGTH);

        // 문자 전송
        smsService.sendSms(phoneNumber,
                SmsVerificationTemplate.CONTENT_PREFIX.getText() +
                        verificationNumber +
                        SmsVerificationTemplate.CONTENT_SUFFIX.getText());

        // Redis에 인증번호 저장
        redisUtil.setDataExpire(member.getId().toString(), verificationNumber, 60 * 10L);

    }

    // 비밀번호 변경
    public void changePassword(Long memberId, String password) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        String encodedPassword = passwordEncoder.encode(password);
        member.savePassword(encodedPassword);

        memberRepository.save(member);
    }

    // 액세스 토큰 + Refresh 토큰 재발급
    public TokenResponse reissueToken(String refreshToken) {

        Long memberId = validateRefreshToken(refreshToken);

        // 기존 Refresh 토큰 삭제 및 재발급
        redisUtil.deleteData(refreshToken);
        String newRefreshToken = createRefreshToken();
        redisUtil.setDataExpire(newRefreshToken, String.valueOf(memberId), rtrProperties.getExpirationLength());

        // 액세스 토큰 재발급
        String accessToken = jwtProvider.createToken(memberId.toString());

        return TokenResponse.from(accessToken, refreshToken);
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

    // Refresh 토큰 검증
    private Long validateRefreshToken(String refreshToken) {

        // Refresh Token이 존재 하지 않는 경우에 대한 예외 처리
        if (!redisUtil.existData(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String memberId = redisUtil.getData(refreshToken);
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(NotFoundMemberException::new);
        return member.getId();
    }

    private String createRefreshToken() {
        return UUID.randomUUID().toString();
    }

}
