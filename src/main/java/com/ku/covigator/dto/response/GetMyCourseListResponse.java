package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetMyCourseListResponse(List<MyCourseList> courses, Boolean hasNext) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record MyCourseList(Long courseId, String name, String description, Double score, String imageUrl, Character isPublic) {
    }

    public static GetMyCourseListResponse from(Slice<Course> courseSlice) {
        List<MyCourseList> courseDtos = courseSlice.getContent().stream()
                .map(course -> MyCourseList.builder()
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
