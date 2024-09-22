package com.ku.covigator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoursePlaceTest {

    @DisplayName("장소 이미지를 추가한다.")
    @Test
    void addImageUrl() {
        //given
        CoursePlace place = CoursePlace.builder().build();

        //when
        place.addImageUrl("www.covi.com");

        //then
        assertThat(place.getImageUrl()).isEqualTo("www.covi.com");
    }
}