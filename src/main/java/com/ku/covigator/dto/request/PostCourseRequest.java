package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.support.GeometryUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostCourseRequest(
        @NotBlank(message = "공백일 수 없습니다.") String courseName,
        String courseDescription,
        List<PlaceDto> places,
        @NotNull(message = "NULL일 수 없습니다.") Character isPublic
) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record PlaceDto(
            @NotBlank(message = "공백일 수 없습니다.") String placeName,
            String address,
            @NotBlank(message = "공백일 수 없습니다.") String category,
            String description,
            @NotNull(message = "NULL일 수 없습니다.") Double latitude,
            @NotNull(message = "NULL일 수 없습니다.") Double longitude) {
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
                        .description(placeDto.description)
                        .address(placeDto.address)
                        .course(course)
                        .coordinate(GeometryUtils.generatePoint(placeDto.latitude, placeDto.longitude))
                        .build())
                .collect(Collectors.toList());
    }
}
