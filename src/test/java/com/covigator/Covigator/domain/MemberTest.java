package com.covigator.Covigator.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @DisplayName("멤버는 기본 'ACTIVE' 상태로 생성된다.")
    @Test
    void memberCreatedInActiveStatus() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        //when //then
        Assertions.assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }
}