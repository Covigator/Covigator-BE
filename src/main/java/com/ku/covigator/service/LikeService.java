package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.Like;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.LikeRepository;
import com.ku.covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addLike(Long memberId, Long courseId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        Like like = buildLike(member, course);

        likeRepository.save(like);

        // 코스의 좋아요 수 업데이트
        course.updateLikeCnt();
        courseRepository.save(course);
    }

    public Like buildLike(Member member, Course course) {
        return Like.builder()
                .member(member)
                .course(course)
                .build();
    }
}
