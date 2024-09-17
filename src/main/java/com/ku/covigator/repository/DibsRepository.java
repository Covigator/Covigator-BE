package com.ku.covigator.repository;

import com.ku.covigator.domain.Dibs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DibsRepository extends JpaRepository<Dibs, Long> {

    Optional<Dibs> findByCourseIdAndMemberId(Long courseId, Long memberId);
}
