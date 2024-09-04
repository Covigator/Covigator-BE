package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Review;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.request.PostReviewRequest;
import com.ku.covigator.dto.response.GetReviewResponse;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CourseRepository courseRepository;

    @AfterEach
    public void tearDown() {
        reviewRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("리뷰를 정상적으로 등록한다.")
    @Test
    void addReview() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .likeCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        PostReviewRequest request = PostReviewRequest.builder()
                .score(5)
                .comment("좋아요~~~")
                .build();

        //when
        reviewService.addReview(savedMember.getId(), savedCourse.getId(), request);

        //then
        List<Review> reviews = reviewRepository.findAll();
        assertAll(
                () -> assertEquals(1, reviews.size()),
                () -> assertThat(reviews.get(0).getComment()).isEqualTo("좋아요~~~"),
                () -> assertThat(reviews.get(0).getScore()).isEqualTo(5),
                () -> assertThat(reviews.get(0).getMember().getId()).isEqualTo(savedMember.getId()),
                () -> assertThat(reviews.get(0).getCourse().getId()).isEqualTo(savedCourse.getId())
        );
    }

    @DisplayName("존재하지 않는 회원에 대한 리뷰 등록 요청 시 예외가 발생한다.")
    @Test
    void addReviewFailsWhenMemberNotFound() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .member(member)
                .description("건대 핫플 리스트")
                .likeCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        PostReviewRequest request = PostReviewRequest.builder()
                .score(5)
                .comment("좋아요~~~")
                .build();

        //when //then
        assertThatThrownBy(
                () -> reviewService.addReview(2L, savedCourse.getId(), request)
        ).isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("존재하지 않는 코스 대한 리뷰 등록 요청 시 예외가 발생한다.")
    @Test
    void addReviewFailsWhenCourseNotFound() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        Member savedMember = memberRepository.save(member);

        PostReviewRequest request = PostReviewRequest.builder()
                .score(5)
                .comment("좋아요~~~")
                .build();

        //when //then
        assertThatThrownBy(
                () -> reviewService.addReview(savedMember.getId(), 1L, request)
        ).isInstanceOf(NotFoundCourseException.class);
    }

    @DisplayName("리뷰 리스트를 조회한다.")
    @Test
    void test() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .likeCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        Review review = Review.builder()
                .score(5)
                .comment("좋아요~~~")
                .member(member)
                .course(course)
                .build();
        reviewRepository.save(review);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetReviewResponse response = reviewService.findReviews(pageable, savedCourse.getId());

        //then
        assertAll(
                () -> assertEquals(response.reviews().size(), 1),
                () -> assertFalse(response.hasNext()),
                () -> assertThat(response.reviews().get(0).author()).isEqualTo("김코비"),
                () -> assertThat(response.reviews().get(0).comment()).isEqualTo("좋아요~~~"),
                () -> assertThat(response.reviews().get(0).score()).isEqualTo(5)
        );
    }

}