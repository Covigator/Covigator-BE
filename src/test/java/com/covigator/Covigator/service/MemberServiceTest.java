package com.covigator.Covigator.service;

import com.covigator.Covigator.domain.Member;
import com.covigator.Covigator.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("중복 회원이 아닌 경우 정상적으로 회원 가입 된다.")
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
        Long savedMemberId = memberService.signUp(member);
        Optional<Member> savedMember = memberRepository.findById(savedMemberId);

        //then
        assertThat(savedMember.get().getEmail()).isEqualTo(member.getEmail());
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