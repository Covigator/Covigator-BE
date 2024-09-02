package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.Comparator;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCourseListResponse(List<CourseDto> courses, Boolean hasNext) {

    @Builder
    public record CourseDto(String name, String description, Double score) {
    }

    public static GetCourseListResponse fromCourseSlice(Slice<Course> courseSlice) {
        List<CourseDto> courseDtos = courseSlice.getContent().stream()
                .map(course -> CourseDto.builder()
                        .name(course.getName())
                        .description(course.getDescription())
                        .score(course.calculateAvgScore())
                        .build()
                ).toList();
        return GetCourseListResponse.builder()
                .courses(courseDtos)
                .hasNext(courseSlice.hasNext())
                .build();
    }
    
}
