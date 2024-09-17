package com.ku.covigator.repository;

import com.ku.covigator.domain.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Slice<Course> findAllCoursesByIsPublic(Pageable pageable, Character isPublic);

    @EntityGraph(attributePaths = "places")
    Optional<Course> findCourseWithPlacesById(Long courseId);

    @Query("""
    SELECT c
    FROM Course c, Dibs d
    WHERE d.member.id = :memberId AND d.course.id = c.id
    ORDER BY d.createdAt DESC
    """)
    Slice<Course> findLikedCoursesByMemberId(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Slice<Course> findMyCoursesByMemberId(Long memberId, Pageable pageable);
}
