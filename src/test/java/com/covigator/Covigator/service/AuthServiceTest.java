package com.covigator.Covigator.service;

import com.covigator.Covigator.domain.Member;
import com.covigator.Covigator.exception.badrequest.PasswordMismatchException;
import com.covigator.Covigator.exception.notfound.NotFoundMemberException;
import com.covigator.Covigator.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("등록된 회원에 대한 유효한 이메일, 비밀번호 입력 시 로그인에 성공한다.")
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
                .build();

        memberRepository.save(member);

        //when
        String token = authService.signIn(member.getEmail(), password);

        //then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @DisplayName("존재하지 않는 회원의 이메일로 로그인을 시도할 경우 예외가 발생한다.")
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
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> authService.signIn(unknownEmail, password))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("유효한 이메일에 대한 잘못된 비밀번호 입력 시 예외가 발생한다.")
    @Test
    void signInWithInvalidPassword() {
        //given
        String password = "covigator123";
        String invalidPassword = "invalidPassword";
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password(password)
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> authService.signIn(member.getEmail(), invalidPassword))
                .isInstanceOf(PasswordMismatchException.class);
    }
}