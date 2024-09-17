package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Dibs;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundDibsException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.DibsRepository;
import com.ku.covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addLike(Long memberId, Long courseId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        Dibs dibs = buildLike(member, course);

        dibsRepository.save(dibs);

        // 코스의 좋아요 수 증가
        course.increaseDibsCnt();
        courseRepository.save(course);
    }

    @Transactional
    public void deleteLike(Long memberId, Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        Dibs dibs = dibsRepository.findByCourseIdAndMemberId(courseId, memberId)
                .orElseThrow(NotFoundDibsException::new);

        dibsRepository.delete(dibs);

        // 코스의 좋아요 수 감소
        course.decreaseDibsCnt();
        courseRepository.save(course);
    }

    public Dibs buildLike(Member member, Course course) {
        return Dibs.builder()
                .member(member)
                .course(course)
                .build();
    }

}
