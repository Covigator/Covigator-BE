package com.ku.covigator.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @DisplayName("코스 별점 평균 값을 업데이트한다.")
    @Test
    void test() {
        //given
        Course course = Course.builder()
                .avgScore(4.0)
                .reviewCnt(5L)
                .build();

        //when
        course.updateAvgScore(4);

        //then
        assertEquals(course.getReviewCnt(), 6L);
        assertEquals(course.getAvgScore(), 4.0);
    }
}