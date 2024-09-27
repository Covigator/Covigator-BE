package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.Dibs;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCommunityCourseListResponse;
import com.ku.covigator.dto.response.GetDibsCourseListResponse;
import com.ku.covigator.dto.response.GetMyCourseListResponse;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CoursePlaceRepository;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.DibsRepository;
import com.ku.covigator.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
    DibsRepository dibsRepository;
    @MockBean
    S3Service s3Service;

    @AfterEach
    void tearDown() {
        coursePlaceRepository.deleteAllInBatch();
        dibsRepository.deleteAllInBatch();
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
                .latitude(123.123123123)
                .longitude(1.111111)
                .build();

        PostCourseRequest.PlaceDto placeDto2 = PostCourseRequest.PlaceDto.builder()
                .placeName("레스티오")
                .category("카페")
                .description("공대생 추천 카페")
                .address("광진구")
                .latitude(123.123123123)
                .longitude(1.111111)
                .build();

        PostCourseRequest postCourseRequest = PostCourseRequest.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .isPublic('Y')
                .places(List.of(placeDto, placeDto2))
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        MockMultipartFile imageFile2 = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        Mockito.when(s3Service.uploadImage(any(MultipartFile.class), any()))
                .thenReturn("https://s3.amazonaws.com/bucket/test-image.jpg");

        //when
        courseService.addCommunityCourse(savedMember.getId(), postCourseRequest, List.of(imageFile, imageFile2));

        //then
        Course course = courseRepository.findAll().get(0);
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
                        .containsExactlyInAnyOrder("광진구", "광진구"),
                () -> assertThat(coursePlaces)
                        .extracting("imageUrl")
                        .containsExactlyInAnyOrder("https://s3.amazonaws.com/bucket/test-image.jpg", "https://s3.amazonaws.com/bucket/test-image.jpg")
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
        assertThatThrownBy(() -> courseService.addCommunityCourse(1L, postCourseRequest, new ArrayList<>()))
                        .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("전체 코스 리스트를 최신순으로 조회한다.")
    @Test
    void findCoursesSortByCreatedAt() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();
        Course savedCourse = courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .build();
        Course savedCourse2 = courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetCommunityCourseListResponse response = courseService.findAllCourses(pageable, savedMember.getId());

        //then
        assertAll(
                () -> assertThat(response.courses()).hasSize(2),
                () -> assertThat(response.courses())
                        .extracting("courseId")
                        .containsExactly(savedCourse2.getId(), savedCourse.getId()),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스2", "건대 풀코스"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트2", "건대 핫플 리스트")
        );
    }

    @DisplayName("전체 코스 리스트를 조회시 좋아요 여부를 판단한다.")
    @Test
    void findCoursesWithLiked() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .build();
        Course savedCourse = courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .build();
        courseRepository.save(course2);

        Dibs dibs = Dibs.builder()
                .course(savedCourse)
                .member(savedMember)
                .build();
        dibsRepository.save(dibs);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        //when
        GetCommunityCourseListResponse response = courseService.findAllCourses(pageable, savedMember.getId());

        //then
        assertAll(
                () -> assertThat(response.courses().size()).isEqualTo(2),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스2", "건대 풀코스"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트2", "건대 핫플 리스트"),
                () -> assertThat(response.courses())
                        .extracting("dibs")
                        .containsExactly(false, true)
        );
    }

    @DisplayName("전체 코스 리스트를 별점순으로 조회한다.")
    @Test
    void findCoursesSortByReviewScore() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .avgScore(5.0)
                .build();
        courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .avgScore(1.0)
                .build();
        courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("avgScore").descending());

        //when
        GetCommunityCourseListResponse response = courseService.findAllCourses(pageable, savedMember.getId());

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
                        .containsExactly(5.0, 1.0)
        );
    }

    @DisplayName("전체 코스 리스트를 좋아요순으로 조회한다.")
    @Test
    void findCoursesSortByLikes() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .build();
        courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .dibsCnt(10L)
                .build();
        courseRepository.save(course2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("DibsCnt").descending());

        //when
        GetCommunityCourseListResponse response = courseService.findAllCourses(pageable, savedMember.getId());

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

    @DisplayName("코스 리스트 조회 시 비공개 코스는 조회되지 않는다.")
    @Test
    void findCoursesOnlyIsPublicTrue() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('N')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .build();

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .dibsCnt(10L)
                .build();
        courseRepository.saveAll(List.of(course, course2));

        Pageable pageable = PageRequest.of(0, 10);

        //when
        GetCommunityCourseListResponse response = courseService.findAllCourses(pageable, savedMember.getId());

        //then
        assertAll(
                () -> assertEquals(response.courses().size(), 1),
                () -> assertThat(response.courses().get(0).name())
                        .isEqualTo("건대 풀코스2"),
                () -> assertThat(response.courses().get(0).description())
                        .isEqualTo("건대 핫플 리스트2")
        );
    }

    @DisplayName("코스 상세 정보를 조회한다.")
    @Test
    void findCourseInfo() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        CoursePlace place = CoursePlace.builder()
                .course(course)
                .address("광진구")
                .name("가츠시")
                .description("공대생 추천 맛집")
                .category("식당")
                .build();

        CoursePlace place2 = CoursePlace.builder()
                .course(course)
                .address("광진구")
                .name("레스티오")
                .description("공대생 추천 카페")
                .category("카페")
                .build();
        coursePlaceRepository.saveAll(List.of(place, place2));

        Dibs dibs = Dibs.builder()
                .course(course)
                .member(member)
                .build();
        dibsRepository.save(dibs);

        //when
        GetCommunityCourseInfoResponse response = courseService.findCourse(savedMember.getId(), savedCourse.getId());

        //then
        assertAll(
                () -> assertThat(response.courseId()).isEqualTo(savedCourse.getId()),
                () -> assertThat(response.courseName()).isEqualTo("건대 풀코스"),
                () -> assertThat(response.courseDescription()).isEqualTo("건대 핫플 리스트"),
                () -> assertThat(response.dibs()).isTrue(),
                () -> assertThat(response.dibsCnt()).isEqualTo(100L),
                () -> assertThat(response.places())
                        .extracting("placeName")
                        .containsExactlyInAnyOrder("가츠시", "레스티오"),
                () -> assertThat(response.places())
                        .extracting("placeDescription")
                        .containsExactlyInAnyOrder("공대생 추천 맛집", "공대생 추천 카페"),
                () -> assertThat(response.places())
                        .extracting("category")
                        .containsExactlyInAnyOrder("식당", "카페")
        );
    }

    @DisplayName("삭제하고자 하는 코스에 포함된 장소와 함께 코스를 삭제한다.")
    @Test
    void test() {
        //given
        Member member = createMember();
        memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        CoursePlace place = CoursePlace.builder()
                .course(course)
                .address("광진구")
                .name("가츠시")
                .description("공대생 추천 맛집")
                .category("식당")
                .build();

        CoursePlace place2 = CoursePlace.builder()
                .course(course)
                .address("광진구")
                .name("레스티오")
                .description("공대생 추천 카페")
                .category("카페")
                .build();
        coursePlaceRepository.saveAll(List.of(place, place2));

        //when
        courseService.deleteCourse(savedCourse.getId());

        //then
        List<Course> courses = courseRepository.findAll();
        List<CoursePlace> places = coursePlaceRepository.findAll();
        assertAll(
                () -> assertEquals(courses.size(), 0),
                () -> assertEquals(places.size(), 0)
        );
    }

    @DisplayName("찜한 코스 리스트를 찜 시간 최신순으로 조회한다.")
    @Test
    void findLikedCourses() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .build();
        Course savedCourse = courseRepository.save(course);

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .dibsCnt(10L)
                .build();
        Course savedCourse2 = courseRepository.save(course2);

        Course course3 = Course.builder()
                .name("건대 풀코스3")
                .isPublic('Y')
                .description("건대 핫플 리스트3")
                .member(member)
                .dibsCnt(10L)
                .build();
        courseRepository.save(course3);

        Dibs dibs = Dibs.builder()
                .member(savedMember)
                .course(savedCourse)
                .build();
        Dibs dibs2 = Dibs.builder()
                .member(savedMember)
                .course(savedCourse2)
                .build();
        dibsRepository.saveAll(List.of(dibs, dibs2));

        //when
        GetDibsCourseListResponse response = courseService.findLikedCourses(savedMember.getId());

        //then
        assertAll(
                () -> assertEquals(2, response.courses().size()),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스2", "건대 풀코스"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트2", "건대 핫플 리스트"),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @DisplayName("마이 코스 리스트를 생성시간 기준 최신순으로 조회한다.")
    @Test
    void findMyCourses() {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .member(member)
                .dibsCnt(100L)
                .isPublic('Y')
                .build();

        Course course2 = Course.builder()
                .name("건대 풀코스2")
                .isPublic('Y')
                .description("건대 핫플 리스트2")
                .member(member)
                .dibsCnt(10L)
                .isPublic('N')
                .build();
        courseRepository.saveAll(List.of(course, course2));

        //when
        GetMyCourseListResponse response = courseService.findMyCourses(savedMember.getId());

        //then
        assertAll(
                () -> assertEquals(2, response.courses().size()),
                () -> assertThat(response.courses())
                        .extracting("name")
                        .containsExactly("건대 풀코스2", "건대 풀코스"),
                () -> assertThat(response.courses())
                        .extracting("description")
                        .containsExactly("건대 핫플 리스트2", "건대 핫플 리스트"),
                () -> assertThat(response.courses())
                        .extracting("isPublic")
                        .containsExactly('N', 'Y'),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    private Member createMember() {
        return Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
    }
}