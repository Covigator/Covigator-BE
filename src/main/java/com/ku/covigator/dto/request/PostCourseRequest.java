package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.member.Member;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostCourseRequest(String courseName, String courseDescription, List<PlaceDto> places, Character isPublic) {

    @Builder
    public record PlaceDto(String placeName, String address, String category, String description) {
    }

    public Course toCourseEntity(Member member) {
        return Course.builder()
                .name(courseName)
                .isPublic(isPublic)
                .description(courseDescription)
                .member(member)
                .build();
    }

    public List<CoursePlace> toCoursePlaceEntity(Course course) {
        return places.stream()
                .map(placeDto -> CoursePlace.builder()
                        .name(placeDto.placeName)
                        .category(placeDto.category)
                        .address(placeDto.address)
                        .description(placeDto.description)
                        .course(course)
                        .build())
                .collect(Collectors.toList());
    }
}
