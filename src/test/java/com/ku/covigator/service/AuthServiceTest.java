package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import com.ku.covigator.security.kakao.KakaoOauthProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @MockBean
    KakaoOauthProvider kakaoOauthProvider;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("등록된 회원에 대한 유효한 이메일, 비밀번호 입력 시 로컬 로그인에 성공한다.")
    @Test
    void signIn() {
        //given
        String password = "covigator123";
        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password(encodedPassword)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when
        String token = authService.signIn(member.getEmail(), password);

        //then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @DisplayName("존재하지 않는 회원의 이메일로 로컬 로그인을 시도할 경우 예외가 발생한다.")
    @Test
    void signInWithUnknownEmail() {
        //given
        String unknownEmail = "abc@naver.com";
        String password = "covigator123";
        Member member = Member.builder()
                .name("김코비")
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
                .name("김코비")
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
    void signUpLocalSuccessIfThereIsNoDuplicateMember() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        //when
        String accessToken = authService.signUp(member);
        Long savedMemberId = Long.parseLong(jwtProvider.getPrincipal(accessToken));

        //then
        assertThat(savedMemberId).isEqualTo(member.getId());
    }

    @DisplayName("로컬 회원 가입 시 같은 플랫폼에 대한 중복 회원은 등록될 수 없다.")
    @Test
    void signUpLocalFailWhenMemberIsDuplicated() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        Member newMember = Member.builder()
                .name("박코비")
                .email("covi@naver.com")
                .password("covigator1234")
                .nickname("covi2")
                .imageUrl("www.covi2.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> authService.signUp(newMember))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 가입된 사용자입니다.");
    }

    @DisplayName("로컬 회원 가입 시 다른 플랫폼에 대한 중복 회원은 등록될 수 있다.")
    @Test
    void signUpLocalFailWhenMemberIsDuplicatedInDifferentPlatform() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.KAKAO)
                .build();

        Member newMember = Member.builder()
                .name("박코비")
                .email("covi@naver.com")
                .password("covigator1234")
                .nickname("covi2")
                .imageUrl("www.covi2.com")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when // then
        assertDoesNotThrow(() -> authService.signUp(newMember));
    }

    @DisplayName("로컬 회원 가입 시 비밀번호는 암호화되어 저장된다.")
    @Test
    void saveEncodedPassword() {
        //given
        String password = "covigator123";
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password(password)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        //when
        authService.signUp(member);

        // then
        assertThat(member.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("이미 가입된 회원에 대한 카카오 로그인 요청 시 기존 회원이 조회된다.")
    @Test
    void returnIsNewFalseWhenExistsMemberLogIn() {
        //given
        String email = "covi@naver.com";
        Member member = Member.builder()
                .name("김코비")
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
                        new KakaoUserInfoResponse.KakaoAccount("name", email,
                                new KakaoUserInfoResponse.KakaoAccount.Profile("nickname", "image")));
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
                .name("김코비")
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
                        new KakaoUserInfoResponse.KakaoAccount("name", email,
                                new KakaoUserInfoResponse.KakaoAccount.Profile("nickname", "image")));
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
                        new KakaoUserInfoResponse.KakaoAccount("name", "email",
                                new KakaoUserInfoResponse.KakaoAccount.Profile("nickname", "image")));
        given(kakaoOauthProvider.getKakaoUserInfo(any())).willReturn(userInfoResponse);

        //when
        KakaoSignInResponse response = authService.signInKakao("code");

        //then
        assertThat(response.isNew()).isEqualTo("True");
    }
}