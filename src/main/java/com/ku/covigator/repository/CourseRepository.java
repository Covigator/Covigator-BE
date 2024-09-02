package com.ku.covigator.repository;

import com.ku.covigator.domain.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = "reviews")
    Slice<Course> findAllCoursesByCreatedAt(Pageable pageable);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.reviews r " +
            "GROUP BY c " +
            "ORDER BY AVG(r.score) DESC")
    Slice<Course> findAllCoursesSortedByAvgScore(Pageable pageable);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.likes l " +
            "GROUP BY c " +
            "ORDER BY COUNT(l) DESC")
    Slice<Course> findAllCoursesSortedByLike(Pageable pageable);
}
