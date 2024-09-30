package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 정보 수정 요청 작업이 정상적으로 실행된다.")
    @Test
    void updateMemberInfo() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123!")
                .nickname("covi")
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when
        memberService.updateMember(member.getId(), "김코비", "covi123!");

        //then
        Member updatedMember = memberRepository.findById(member.getId()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("김코비");
    }

    @DisplayName("회원 정보 수정 요청 시 중복된 닉네임이 존재하는 경우 예외가 발생한다.")
    @Test
    void badRequestExceptionOccursWhenDuplicatedNicknameExistsWhileUpdating() {
        //given
        String nickName = "covi";

        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123!")
                .nickname(nickName)
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(
                () -> memberService.updateMember(member.getId(), nickName, "covi123!")
        ).isInstanceOf(DuplicateMemberNicknameException.class);
    }

    @DisplayName("존재하지 않는 회원에 대한 회원 정보 수정 요청 시 예외가 발생한다.")
    @Test
    void notFoundExceptionOccursWhenMemberNotExists() {
        //when //then
        assertThatThrownBy(
                () -> memberService.updateMember(1L, "covi", "covi123!")
        ).isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("중복된 닉네임이 존재하는 경우 예외가 발생한다.")
    @Test
    void badRequestExceptionOccursWhenDuplicatedNicknameExists() {
        //given
        String nickName = "covi";

        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123!")
                .nickname(nickName)
                .platform(Platform.LOCAL)
                .build();

        memberRepository.save(member);

        //when //then
        assertThatThrownBy(
                () -> memberService.verifyNicknameDuplication(nickName)
        ).isInstanceOf(DuplicateMemberNicknameException.class);
    }

}