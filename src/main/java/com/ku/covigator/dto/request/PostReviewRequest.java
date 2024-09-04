package com.ku.covigator.dto.request;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Review;
import com.ku.covigator.domain.member.Member;
import lombok.Builder;

@Builder
public record PostReviewRequest(Integer score, String comment) {

    public Review toEntity(Member member, Course course) {
        return Review.builder()
                .course(course)
                .member(member)
                .comment(comment)
                .score(score)
                .build();
    }
}
