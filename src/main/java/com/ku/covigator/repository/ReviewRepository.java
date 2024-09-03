package com.ku.covigator.repository;

import com.ku.covigator.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "member")
    List<Review> findByCourseId(Long courseId);
}
