package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Review;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.dto.request.PostReviewRequest;
import com.ku.covigator.dto.response.GetReviewResponse;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void addReview(Long memberId, Long courseId, PostReviewRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        Review review = request.toEntity(member, course);

        reviewRepository.save(review);

        // 코스의 평균 별점 값 update
        course.updateAvgScore(review.getScore());
        courseRepository.save(course);
    }

    public GetReviewResponse findReviews(Pageable pageable, Long courseId) {
        Slice<Review> reviews = reviewRepository.findByCourseId(pageable, courseId);
        return GetReviewResponse.fromEntity(reviews);
    }
}
