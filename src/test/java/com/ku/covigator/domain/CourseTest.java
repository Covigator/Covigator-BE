package com.ku.covigator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
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

    @DisplayName("썸네일 이미지가 존재하지 않는 경우 null을 반환한다.")
    @Test
    void getNullThumbnailImage() {
        //given
        Course course = Course.builder().build();

        //when
        String thumbnailImage = course.getThumbnailImage();

        //then
        assertNull(thumbnailImage);
    }

    @DisplayName("썸네일 이미지가 존재하면 url을 반환한다.")
    @Test
    void getThumbnailImage() {
        //given
        Course course = Course.builder().build();
        CoursePlace place = CoursePlace.builder()
                .course(course)
                .address("광진구")
                .name("가츠시")
                .description("공대생 추천 맛집")
                .category("식당")
                .imageUrl("www.image.com")
                .build();
        course.addPlace(place);

        //when //then
        String thumbnailImage = course.getThumbnailImage();

        //then
        assertThat(thumbnailImage).isEqualTo("www.image.com");
    }

}