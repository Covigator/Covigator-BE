package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.Like;
import com.ku.covigator.domain.Review;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCourseListResponse;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.*;
import org.junit.jupiter.api.BeforeEach;
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
class CourseServiceTest {

    @Autowired
    CourseService courseService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    CoursePlaceRepository coursePlaceRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    LikeRepository likeRepository;

    @BeforeEach()
    void tearDown() {
        coursePlaceRepository.deleteAllInBatch();
        likeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("코스와 장소를 정상적으로 등록한다.")
    @Test
    void addCourse() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        PostCourseRequest.PlaceDto placeDto = PostCourseRequest.PlaceDto.builder()
                .placeName("가츠시")
                .category("식당")
                .description("공대생 추천 맛집")
                .address("광진구")
                .build();

        PostCourseRequest.PlaceDto placeDto2 = PostCourseRequest.PlaceDto.builder()
                .placeName("레스티오")
                .category("카페")
                .description("공대생 추천 카페")
                .address("광진구")
                .build();

        PostCourseRequest postCourseRequest = PostCourseRequest.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .isPublic('Y')
                .places(List.of(placeDto, placeDto2))
                .build();

        //when
        courseService.addCommunityCourse(savedMember.getId(), postCourseRequest);

        //then
        Course course = courseRepository.findAll().getFirst();
        List<CoursePlace> coursePlaces = coursePlaceRepository.findAll();

        assertAll(
                () -> assertThat(course.getName()).isEqualTo("건대 풀코스"),
                () -> assertThat(course.getDescription()).isEqualTo("건대 핫플 요약 코스"),
                () -> assertThat(course.getIsPublic()).isEqualTo('Y'),
                () -> assertThat(course.getMember().getId()).isEqualTo(savedMember.getId()),
                () -> assertThat(coursePlaces)
                        .extracting("name")
                        .containsExactlyInAnyOrder("가츠시", "레스티오"),
                () -> assertThat(coursePlaces)
                        .extracting("category")
                        .containsExactlyInAnyOrder("식당", "카페"),
                () -> assertThat(coursePlaces)
                        .extracting("description")
                        .containsExactlyInAnyOrder("공대생 추천 맛집", "공대생 추천 카페"),
                () -> assertThat(coursePlaces)
                        .extracting("address")
                        .containsExactlyInAnyOrder("광진구", "광진구")
        );
    }

    @DisplayName("존재하지 않는 회원에 대한 코스 등록 시 예외가 발생한다.")
    @Test
    void addCourseFailsWhenMemberNotExists() {
        //given
        PostCourseRequest.PlaceDto placeDto = PostCourseRequest.PlaceDto.builder()
                .placeName("가츠시")
                .category("식당")
                .description("공대생 추천 맛집")
                .address("광진구")
                .build();

        PostCourseRequest.PlaceDto placeDto2 = PostCourseRequest.PlaceDto.builder()
                .placeName("레스티오")
                .category("카페")
                .description("공대생 추천 카페")
                .address("광진구")
                .build();

        PostCourseRequest postCourseRequest = PostCourseRequest.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .isPublic('Y')
                .places(List.of(placeDto, placeDto2))
                .build();

        //when //then
        assertThatThrownBy(() -> courseService.addCommunityCourse(1L, postCourseRequest))
                        .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("전체 코스 리스트를 최신순으로 조회한다.")
    @Test
    void findCoursesSortByCreatedAt() {
        //given
        Member member = createMember();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();
        courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .build();
        courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetCourseListResponse response = courseService.findAllCourses(pageable, null);

        //then
        assertAll(
                () -> assertThat(response.courses().size()).isEqualTo(2),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스2", "건대 풀코스"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트2", "건대 핫플 리스트")
        );
    }

    @DisplayName("전체 코스 리스트를 별점순으로 조회한다.")
    @Test
    void findCoursesSortByReviewScore() {
        //given
        Member member = createMember();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();
        courseRepository.save(course);

        Review review = Review.builder()
                .course(course)
                .score(5)
                .comment("굿")
                .member(member)
                .build();
        reviewRepository.save(review);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .build();
        courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetCourseListResponse response = courseService.findAllCourses(pageable, "score");

        //then
        assertAll(
                () -> assertThat(response.courses().size()).isEqualTo(2),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스", "건대 풀코스2"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트", "건대 핫플 리스트2"),
                () -> assertThat(response.courses())
                        .extracting("score")
                        .containsExactly(5.0, 0.0)
        );
    }

    @DisplayName("전체 코스 리스트를 좋아요순으로 조회한다.")
    @Test
    void findCoursesSortByLikes() {
        //given
        Member member = createMember();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();
        courseRepository.save(course);

        Like like = Like.builder()
                .member(member)
                .course(course)
                .build();
        likeRepository.save(like);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .build();
        courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetCourseListResponse response = courseService.findAllCourses(pageable, "like");

        //then
        assertAll(
                () -> assertThat(response.courses().size()).isEqualTo(2),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스", "건대 풀코스2"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트", "건대 핫플 리스트2")
        );
    }

    private Member createMember() {
        return Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
    }
}