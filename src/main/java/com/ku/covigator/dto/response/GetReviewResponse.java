package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.Review;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetReviewResponse(List<ReviewDto> reviews, Boolean hasNext) {

    @Builder
    public record ReviewDto(String author, Integer score, String comment) {

    }

    public static GetReviewResponse fromEntity(Slice<Review> reviews) {
        List<ReviewDto> reviewDtos = reviews.stream().map(
                review -> ReviewDto.builder()
                        .author(review.getMember().getName())
                        .comment(review.getComment())
                        .score(review.getScore())
                        .build()
        ).toList();
        return new GetReviewResponse(reviewDtos, reviews.hasNext());
    }

}
