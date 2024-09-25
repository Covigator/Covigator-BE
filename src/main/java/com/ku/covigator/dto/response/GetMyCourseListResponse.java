package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetMyCourseListResponse(List<CourseDto> courses, Boolean hasNext) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CourseDto(Long courseId, String name, String description, Double score, String imageUrl, Character isPublic) {
    }

    public static GetMyCourseListResponse from(Slice<Course> courseSlice) {
        List<CourseDto> courseDtos = courseSlice.getContent().stream()
                .map(course -> CourseDto.builder()
                        .name(course.getName())
                        .description(course.getDescription())
                        .score(course.getAvgScore())
                        .courseId(course.getId())
                        .imageUrl(course.getThumbnailImage())
                        .isPublic(course.getIsPublic())
                        .build()
                ).toList();
        return GetMyCourseListResponse.builder()
                .courses(courseDtos)
                .hasNext(courseSlice.hasNext())
                .build();
    }
}
