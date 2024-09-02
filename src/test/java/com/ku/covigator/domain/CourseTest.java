package com.ku.covigator.domain;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CourseTest {

    @DisplayName("코스 별점 평균값을 계산한다.")
    @Test
    void calculateAvgScore() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();

        Review review = Review.builder()
                .course(course)
                .score(5)
                .comment("굿")
                .member(member)
                .build();

        Review review2 = Review.builder()
                .course(course)
                .score(4)
                .comment("굿")
                .member(member)
                .build();

        course.addReview(review);
        course.addReview(review2);

        //when //then
        Assertions.assertThat(course.calculateAvgScore()).isEqualTo(4.5);
    }
}