package com.ku.covigator.service;

import com.ku.covigator.domain.Member;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JwtProvider jwtProvider;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("중복 회원이 아닌 경우 정상적으로 회원 가입 되어 토큰을 반환한다.")
    @Test
    void signUpSuccessIfThereIsNoDuplicateMember() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        //when
        String accessToken = memberService.signUp(member);
        Long savedMemberId = Long.parseLong(jwtProvider.getPrincipal(accessToken));

        //then
        assertThat(savedMemberId).isEqualTo(member.getId());
    }

    @DisplayName("회원 가입 시 중복 회원은 등록될 수 없다.")
    @Test
    void signUpFailWhenMemberIsDuplicated() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        Member newMember = Member.builder()
                .name("박코비")
                .email("covi@naver.com")
                .password("covigator1234")
                .nickname("covi2")
                .imageUrl("www.covi2.com")
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> memberService.signUp(newMember))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 가입된 사용자입니다.");
    }

    @DisplayName("회원 가입 시 비밀번호는 암호화되어 저장된다.")
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
                .build();

        //when
        memberService.signUp(member);

        // then
        assertThat(member.getPassword()).isNotEqualTo(password);
    }
}