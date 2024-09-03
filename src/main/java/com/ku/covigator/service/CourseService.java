package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCourseListResponse;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.CoursePlaceRepository;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePlaceRepository coursePlaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addCommunityCourse(Long memberId, PostCourseRequest request) {
        //회원 조회
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);

        //코스 등록
        Course course = request.toCourseEntity(member);
        courseRepository.save(course);

        //코스_장소 등록
        List<CoursePlace> coursePlaces = request.toCoursePlaceEntity(course);
        coursePlaceRepository.saveAll(coursePlaces);
    }

    @Transactional(readOnly = true)
    public GetCourseListResponse findAllCourses(Pageable pageable) {

        Slice<Course> courses = courseRepository.findAllCoursesBy(pageable);
        return GetCourseListResponse.fromCourseSlice(courses);

    }

    @Transactional(readOnly = true)
    public GetCommunityCourseInfoResponse findCourse(Long memberId, Long courseId) {

        Member member = memberRepository.findMemberWithLikesById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        Course course = courseRepository.findCourseWithPlacesById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        //좋아요 여부 확인
        boolean isLiked = checkIfLiked(courseId, member);

        return GetCommunityCourseInfoResponse.from(course, isLiked);
    }

    private boolean checkIfLiked(Long courseId, Member member) {
        return member.getLikes().stream().anyMatch(
                like -> like.getCourse().getId().equals(courseId)
        );
    }

}
