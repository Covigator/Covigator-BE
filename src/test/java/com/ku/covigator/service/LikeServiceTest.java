package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Like;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.LikeRepository;
import com.ku.covigator.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class LikeServiceTest {

    @Autowired
    private LikeService likeService;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CourseRepository courseRepository;

    @AfterEach
    public void tearDown() {
        likeRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("좋아요 클릭 시 정상적으로 좋아요가 추가된다.")
    @Test
    void addLike() {
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

        //when
        likeService.addLike(savedMember.getId(), savedCourse.getId());

        //then
        List<Like> likes = likeRepository.findAll();
        assertAll(
                () -> assertEquals(1, likes.size()),
                () -> assertThat(likes.get(0).getMember().getName()).isEqualTo("김코비"),
                () -> assertThat(likes.get(0).getCourse().getName()).isEqualTo("건대 풀코스")
        );
    }

    @DisplayName("좋아요 클릭 시 코스에 좋아요 수가 올라간다.")
    @Test
    void updateCourseLikeCntWhenLikeClicked() {
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

        //when
        likeService.addLike(savedMember.getId(), savedCourse.getId());

        //then
        Course foundCourse = courseRepository.findById(savedCourse.getId()).get();
        Assertions.assertEquals(101L, foundCourse.getLikeCnt());
    }

    @DisplayName("존재하지 않는 회원에 대한 좋아요 요청 시 예외가 발생한다.")
    @Test
    void addLikeFailsWhenMemberNotFound() {
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

        //when //then
        assertThatThrownBy(
                () -> likeService.addLike(100L, savedCourse.getId())
        ).isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("존재하지 않는 코스 대한 리뷰 등록 요청 시 예외가 발생한다.")
    @Test
    void addLikeFailsWhenCourseNotFound() {
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

        //when //then
        assertThatThrownBy(
                () -> likeService.addLike(savedMember.getId(), 100L)
        ).isInstanceOf(NotFoundCourseException.class);
    }

}