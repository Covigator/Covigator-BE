package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetCommunityCourseListResponse(List<CourseDto> courses, Boolean hasNext) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CourseDto(Long courseId, String name, String description, Double score, Boolean isLiked) {
    }

    public static GetCommunityCourseListResponse from(Slice<Course> courseSlice, Set<Long> likedCourseIds) {
        List<CourseDto> courseDtos = courseSlice.getContent().stream()
                .map(course -> CourseDto.builder()
                        .courseId(course.getId())
                        .name(course.getName())
                        .description(course.getDescription())
                        .score(course.getAvgScore())
                        .isLiked(likedCourseIds.contains(course.getId())) // 좋아요 여부 판단
                        .build()
                ).collect(Collectors.toList());

        return GetCommunityCourseListResponse.builder()
                .courses(courseDtos)
                .hasNext(courseSlice.hasNext())
                .build();
    }

}
