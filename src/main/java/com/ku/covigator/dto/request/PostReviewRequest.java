package com.ku.covigator.dto.request;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Review;
import com.ku.covigator.domain.member.Member;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record PostReviewRequest(
        @NotNull(message = "NULL일 수 없습니다.")
        @Min(value = 1, message = "값이 1보다 작을 수 없습니다.")
        @Max(value = 5, message = "값이 5보다 클 수 없습니다.") Integer score,

        @NotBlank(message = "공백일 수 없습니다.")
        @Size(min = 1, max = 150, message = "글자 길이는 1~150자여야 합니다.") String comment) {

    public Review toEntity(Member member, Course course) {
        return Review.builder()
                .course(course)
                .member(member)
                .comment(comment)
                .score(score)
                .build();
    }
}
