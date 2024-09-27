package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCommunityCourseInfoResponse(Long courseId, String courseName, String courseDescription, Long dibsCnt,
                                             Boolean dibs, List<PlaceList> places) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record PlaceList(Long placeId, String placeName, String placeDescription, String category,
                            String imageUrl, Double latitude, Double longitude) {
    }

    public static GetCommunityCourseInfoResponse from(Course course, boolean dibs) {
        List<PlaceList> places = course.getPlaces().stream()
                .map(place -> PlaceList.builder()
                        .placeId(place.getId())
                        .placeDescription(place.getDescription())
                        .placeName(place.getName())
                        .category(place.getCategory())
                        .imageUrl(place.getImageUrl())
                        .latitude(place.getCoordinate().getY())
                        .longitude(place.getCoordinate().getX())
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
