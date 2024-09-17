package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCommunityCourseInfoResponse(Long courseId, String courseName, String courseDescription, Long dibsCnt,
                                             Boolean dibs, List<PlaceDto> places) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record PlaceDto(Long placeId, String placeName, String placeDescription, String category) {
    }

    public static GetCommunityCourseInfoResponse from(Course course, boolean dibs) {
        List<PlaceDto> places = course.getPlaces().stream()
                .map(place -> PlaceDto.builder()
                        .placeId(place.getId())
                        .placeDescription(place.getDescription())
                        .placeName(place.getName())
                        .category(place.getCategory())
                        .build()
                ).toList();
        return GetCommunityCourseInfoResponse.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .courseDescription(course.getDescription())
                .dibsCnt(course.getDibsCnt())
                .dibs(dibs)
                .places(places)
                .build();
    }
}
