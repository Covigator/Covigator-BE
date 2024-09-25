package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Course;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetDibsCourseListResponse(List<DibsCourseList> courses, Boolean hasNext) {

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record DibsCourseList(Long courseId, String name, String description, Double score, String imageUrl) {
    }

    public static GetDibsCourseListResponse from(Slice<Course> courseSlice) {
        List<DibsCourseList> courseDtos = courseSlice.getContent().stream()
                .map(course -> DibsCourseList.builder()
                        .name(course.getName())
                        .description(course.getDescription())
                        .score(course.getAvgScore())
                        .courseId(course.getId())
                        .imageUrl(course.getThumbnailImage())
                        .build()
                ).toList();
        return GetDibsCourseListResponse.builder()
                .courses(courseDtos)
                .hasNext(courseSlice.hasNext())
                .build();
    }
    
}
