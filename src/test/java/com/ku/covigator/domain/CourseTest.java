package com.ku.covigator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @DisplayName("코스 별점 평균 값을 업데이트한다.")
    @Test
    void updateAvgScore() {
        //given
        Course course = Course.builder()
                .avgScore(4.0)
                .reviewCnt(5L)
                .build();

        //when
        course.updateAvgScore(4);

        //then
        assertEquals(6L, course.getReviewCnt());
        assertEquals(4.0, course.getAvgScore());
    }

    @DisplayName("코스의 좋아요 수를 업데이트한다.")
    @Test
    void updateLikeCnt() {
        //given
        Course course = Course.builder()
                .likeCnt(0L)
                .build();

        //when
        course.updateLikeCnt();

        //then
        assertEquals(1L, course.getLikeCnt());
    }
}