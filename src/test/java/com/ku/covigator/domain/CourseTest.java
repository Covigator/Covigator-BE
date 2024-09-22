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

    @DisplayName("코스의 좋아요 수를 1 증가시킨다.")
    @Test
    void increaseLikeCnt() {
        //given
        Course course = Course.builder()
                .dibsCnt(0L)
                .build();

        //when
        course.increaseDibsCnt();

        //then
        assertEquals(1L, course.getDibsCnt());
    }

    @DisplayName("코스의 좋아요 수를 1 감소시킨다.")
    @Test
    void decreaseLikeCnt() {
        //given
        Course course = Course.builder()
                .dibsCnt(1L)
                .build();

        //when
        course.decreaseDibsCnt();

        //then
        assertEquals(0L, course.getDibsCnt());
    }

}