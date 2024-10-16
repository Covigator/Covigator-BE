package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.dto.response.TokenResponse;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.badrequest.InvalidRefreshTokenException;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundEmailException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import com.ku.covigator.security.kakao.KakaoOauthProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    RedisUtil redisUtil;
    @MockBean
    KakaoOauthProvider kakaoOauthProvider;
    @MockBean
    S3Service s3Service;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
        redisUtil.deleteAll();
    }

    @DisplayName("등록된 회원에 대한 유효한 이메일, 비밀번호 입력 시 로컬 로그인에 성공한다.")
    @Test
    void signIn() {
        //given
        String password = "covigator123";
        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.builder()
                .email("covi@naver.com")
                .password(encodedPassword)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when
        TokenResponse response = authService.signIn(member.getEmail(), password);

        //then
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertFalse(response.accessToken().isBlank());
        assertFalse(response.refreshToken().isBlank());
    }

    @DisplayName("존재하지 않는 회원의 이메일로 로컬 로그인을 시도할 경우 예외가 발생한다.")
    @Test
    void signInWithUnknownEmail() {
        //given
        String unknownEmail = "abc@naver.com";
        String password = "covigator123";
        Member member = Member.builder()
                .email("covi@naver.com")
                .password(password)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> authService.signIn(unknownEmail, password))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("로컬 로그인 시도 중 유효한 이메일에 대한 잘못된 비밀번호 입력 시 예외가 발생한다.")
    @Test
    void signInWithInvalidPassword() {
        //given
        String email = "covi@naver.com";
        String password = "covigator123";
        String invalidPassword = "invalidPassword";
        Member member = Member.builder()
                .email(email)
                .password(password)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> authService.signIn(email, invalidPassword))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @DisplayName("로컬 회원 가입 시 같은 플랫폼에 대한 중복 회원이 아닌 경우 정상적으로 회원 가입 되어 토큰을 반환한다.")
    @Test
    void signUpLocalSuccessIfThereIsNoDuplicatedMember() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        Mockito.when(s3Service.uploadImage(any(MultipartFile.class), any()))
                .thenReturn("https://s3.amazonaws.com/bucket/test-image.jpg");

        //when
        TokenResponse response = authService.signUp(member, imageFile);
        Long savedMemberId = Long.parseLong(jwtProvider.getPrincipal(response.accessToken()));

        //then
        assertThat(savedMemberId).isEqualTo(member.getId());
    }

    @DisplayName("중복된 닉네임에 대한 로컬 회원 가입 요청 시 예외가 발생한다.")
    @Test
    void signUpLocalFailIfThereIsDuplicatedNickname() {
        //given
        String nickname = "covi";

        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname(nickname)
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        Member newMember = Member.builder()
                .email("covi2@naver.com")
                .password("covi123!")
                .nickname(nickname)
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        //when //then
        assertThatThrownBy(
                () -> authService.signUp(newMember, null)
        ).isInstanceOf(DuplicateMemberNicknameException.class);
    }

    @DisplayName("로컬 회원 가입 시 같은 플랫폼에 대한 중복 회원은 등록될 수 없다.")
    @Test
    void signUpLocalFailWhenMemberIsDuplicated() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        Member newMember = Member.builder()
                .email("covi@naver.com")
                .password("covigator1234")
                .nickname("covi2")
                .imageUrl("www.covi2.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        Mockito.when(s3Service.uploadImage(any(MultipartFile.class), any()))
                .thenReturn("https://s3.amazonaws.com/bucket/test-image.jpg");

        //when //then
        assertThatThrownBy(() -> authService.signUp(newMember, imageFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 가입된 사용자입니다.");
    }

    @DisplayName("로컬 회원 가입 시 다른 플랫폼에 대한 중복 회원은 등록될 수 있다.")
    @Test
    void signUpLocalFailWhenMemberIsDuplicatedInDifferentPlatform() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.KAKAO)
                .build();

        Member newMember = Member.builder()
                .email("covi@naver.com")
                .password("covigator1234")
                .nickname("covi2")
                .imageUrl("www.covi2.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        Mockito.when(s3Service.uploadImage(any(MultipartFile.class), any()))
                .thenReturn("https://s3.amazonaws.com/bucket/test-image.jpg");

        //when // then
        assertDoesNotThrow(() -> authService.signUp(newMember, imageFile));
    }

    @DisplayName("로컬 회원 가입 시 비밀번호는 암호화되어 저장된다.")
    @Test
    void saveEncodedPassword() {
        //given
        String password = "covigator123";
        Member member = Member.builder()
                .email("covi@naver.com")
                .password(password)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        Mockito.when(s3Service.uploadImage(any(MultipartFile.class), any()))
                .thenReturn("https://s3.amazonaws.com/bucket/test-image.jpg");

        //when
        authService.signUp(member, imageFile);

        // then
        assertThat(member.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("이미 가입된 회원에 대한 카카오 로그인 요청 시 기존 회원이 조회된다.")
    @Test
    void returnIsNewFalseWhenExistsMemberLogIn() {
        //given
        String email = "covi@naver.com";
        Member member = Member.builder()
                .email(email)
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.KAKAO)
                .build();

        memberRepository.save(member);

        KakaoTokenResponse tokenResponse = new KakaoTokenResponse("token", 100);
        given(kakaoOauthProvider.getKakaoToken("code")).willReturn(tokenResponse);

        KakaoUserInfoResponse userInfoResponse =
                new KakaoUserInfoResponse(
                        new KakaoUserInfoResponse.KakaoAccount(email,
                                new KakaoUserInfoResponse.KakaoAccount.Profile("image")));
        given(kakaoOauthProvider.getKakaoUserInfo(any())).willReturn(userInfoResponse);

        //when
        KakaoSignInResponse response = authService.signInKakao("code");

        //then
        assertThat(response.isNew()).isEqualTo("False");
    }

    @DisplayName("로컬에서 이미 가입된 회원에 대한 카카오 로그인 요청 시 신규 회원으로 등록된다.")
    @Test
    void registerNewMemberWhenKakaoLoginDespiteOfLocalMemberExists() {
        //given
        String email = "covi@naver.com";
        Member member = Member.builder()
                .email(email)
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        KakaoTokenResponse tokenResponse = new KakaoTokenResponse("token", 100);
        given(kakaoOauthProvider.getKakaoToken("code")).willReturn(tokenResponse);

        KakaoUserInfoResponse userInfoResponse =
                new KakaoUserInfoResponse(
                        new KakaoUserInfoResponse.KakaoAccount(email,
                                new KakaoUserInfoResponse.KakaoAccount.Profile("image")));
        given(kakaoOauthProvider.getKakaoUserInfo(any())).willReturn(userInfoResponse);

        //when
        KakaoSignInResponse response = authService.signInKakao("code");

        //then
        assertThat(response.isNew()).isEqualTo("True");
    }

    @DisplayName("신규 회원에 대한 카카오 로그인 요청 시 새로 가입된다.")
    @Test
    void returnIsNewTrueWhenNewMemberLogIn() {
        //given
        KakaoTokenResponse tokenResponse = new KakaoTokenResponse("token", 100);
        given(kakaoOauthProvider.getKakaoToken("code")).willReturn(tokenResponse);

        KakaoUserInfoResponse userInfoResponse =
                new KakaoUserInfoResponse(
                        new KakaoUserInfoResponse.KakaoAccount("email",
                                new KakaoUserInfoResponse.KakaoAccount.Profile("image")));
        given(kakaoOauthProvider.getKakaoUserInfo(any())).willReturn(userInfoResponse);

        //when
        KakaoSignInResponse response = authService.signInKakao("code");

        //then
        assertThat(response.isNew()).isEqualTo("True");
    }

    @DisplayName("등록되지 않은 이메일에 대한 인증번호 발송 요청시 예외가 발생한다.")
    @Test
    void notFoundEmailExceptionOccursWhenMemberIsNotRegistered() {
        //when //then
        assertThatThrownBy(
                () -> authService.createVerificationNumber("covi@naver.com")
        ).isInstanceOf(NotFoundEmailException.class);
    }

    @DisplayName("[이메일] 인증번호 생성시 Redis에 인증번호가 정상적으로 저장된다.")
    @Test
    void emailVerificationCodeSavedInRedis() {
        //given
        String email = "covi@naver.com";
        Member member = Member.builder()
                .email(email)
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        memberRepository.save(member);

        //when
        authService.createVerificationNumber(email);

        //then
        assertThat(redisUtil.existData(email)).isTrue();
    }

    @DisplayName("등록되지 않은 사용자에 대한 비밀번호 변경 요청시 예외가 발생한다.")
    @Test
    void changePasswordFailsWhenMemberIsNotFound() {
        //when //then
        assertThatThrownBy(
                () -> authService.changePassword(1L, "covi123!")
        );
    }

    @DisplayName("비밀번호를 변경한다.")
    @Test
    void changePassword() {
        //given
        String oldPassword = "covigator1!";
        Member member = Member.builder()
                .email("covi@naver.com")
                .password(oldPassword)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        Member savedMember = memberRepository.save(member);

        //when
        authService.changePassword(savedMember.getId(), "covi123!");

        //then
        String newPassword = memberRepository.findById(savedMember.getId()).get().getPassword();
        assertThat(oldPassword).isNotEqualTo(newPassword);
    }

    @DisplayName("유효하지 않은 refresh 토큰 전달 시, 토큰 재발급 요청에 실패한다.")
    @Test
    void invalidRefreshToken() {
        //when //then
        assertThatThrownBy(() -> authService.reissueToken("refresh_token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @DisplayName("존재하지 않는 회원에 대한 refresh 토큰 재발급 요청이 실패한다.")
    @Test
    void refreshTokenForMemberNotExists() {
        //given
        redisUtil.setDataExpire("refresh_token", "100", 3600 * 1000);

        //when //then
        assertThatThrownBy(() -> authService.reissueToken("refresh_token"))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("토큰을 재발급한다.")
    @Test
    void reissueToken() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.KAKAO)
                .build();
        Member savedMember = memberRepository.save(member);

        redisUtil.setDataExpire("refresh_token", savedMember.getId().toString(), 3600 * 1000);

        //when
        TokenResponse response = authService.reissueToken("refresh_token");

        //then
        assertThat(redisUtil.existData("refresh_token")).isFalse();
        assertThat(response.refreshToken()).isNotNull();
        assertThat(response.accessToken()).isNotNull();
    }

    @DisplayName("존재하지 않는 회원에 대한 인증 번호 문자 전송 요청은 실패한다.")
    @Test
    void sendSmsFailForMemberNotExists() {
        //when //then
        assertThatThrownBy(() -> authService.sendMessage(1L, "01012341234"))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("[문자] 인증번호 생성시 Redis에 인증번호가 정상적으로 저장된다.")
    @Test
    void smsVerificationCodeSavedInRedis() {
        //given
        Member member = Member.builder()
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        Member savedMember = memberRepository.save(member);
        Long memberId = savedMember.getId();

        //when
        authService.sendMessage(memberId, "01012341234");

        //then
        assertThat(redisUtil.existData(memberId.toString())).isTrue();
    }

}