package com.ku.covigator.repository;

import com.ku.covigator.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "member")
    Slice<Review> findByCourseId(Pageable pageable, Long courseId);
}
