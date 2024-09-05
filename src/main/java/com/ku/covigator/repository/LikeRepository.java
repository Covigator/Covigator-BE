package com.ku.covigator.repository;

import com.ku.covigator.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByCourseIdAndMemberId(Long courseId, Long memberId);
}
