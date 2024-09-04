package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostReviewRequest;
import com.ku.covigator.dto.response.GetReviewResponse;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰")
@RestController
@RequestMapping("/community/courses/{course_id}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록")
    @PostMapping
    public ResponseEntity<Void> addReview(
            @LoggedInMemberId Long memberId,
            @PathVariable(name = "course_id") Long courseId,
            @RequestBody PostReviewRequest request) {
        reviewService.addReview(memberId, courseId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 조회")
    @GetMapping
    public ResponseEntity<GetReviewResponse> findReviews(
            @PageableDefault(
                    page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable,
            @PathVariable(name = "course_id") Long courseId) {
        return ResponseEntity.ok(reviewService.findReviews(pageable, courseId));
    }
}
