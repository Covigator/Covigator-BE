package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCommunityCourseInfoResponse(String courseName, String courseDescription, Long likeCnt, Boolean isLiked,
                                             List<PlaceDto> places) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record PlaceDto(String placeName, String placeDescription, String category) {
    }

    public static GetCommunityCourseInfoResponse from(Course course, boolean isLiked) {
        List<PlaceDto> places = course.getPlaces().stream()
                .map(place -> PlaceDto.builder()
                        .placeDescription(place.getDescription())
                        .placeName(place.getName())
                        .category(place.getCategory())
                        .build()
                ).toList();
        return GetCommunityCourseInfoResponse.builder()
                .courseName(course.getName())
                .courseDescription(course.getDescription())
                .likeCnt(course.getLikeCnt())
                .isLiked(isLiked)
                .places(places)
                .build();
    }
}
