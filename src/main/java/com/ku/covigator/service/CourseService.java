package com.ku.covigator.service;

import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.CoursePlace;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCourseListResponse;
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
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePlaceRepository coursePlaceRepository;
    private final MemberRepository memberRepository;

    private final Map<String, Function<Pageable, Slice<Course>>> filterMap = Map.of(
            "score", this::findAllCoursesSortedByAvgScore,
            "like", this::findAllCoursesSortedByLike
    );

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
    public GetCourseListResponse findAllCourses(Pageable pageable, String filter) {

        Slice<Course> courseSlice = filterMap
                .getOrDefault(filter != null ? filter : "", this::findAllCoursesSortedByCreatedAt)
                .apply(pageable);

        return GetCourseListResponse.fromCourseSlice(courseSlice);
    }

    private Slice<Course> findAllCoursesSortedByAvgScore(Pageable pageable) {
        return courseRepository.findAllCoursesSortedByAvgScore(pageable);
    }

    private Slice<Course> findAllCoursesSortedByLike(Pageable pageable) {
        return courseRepository.findAllCoursesSortedByLike(pageable);
    }

    private Slice<Course> findAllCoursesSortedByCreatedAt(Pageable pageable) {
        return courseRepository.findAllCoursesByCreatedAt(pageable);
    }

}
