package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CoursePlaceRepository;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceTest {

    @Autowired CourseService courseService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    CoursePlaceRepository coursePlaceRepository;

    @BeforeEach()
    void tearDown() {
        coursePlaceRepository.deleteAllInBatch();
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