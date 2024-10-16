package com.ku.covigator.repository;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPlatform(String email, Platform platform);

    @Query("""
    SELECT m
    FROM Member m
    LEFT JOIN FETCH m.dibs d
    LEFT JOIN FETCH d.course c
    WHERE m.id=:memberId
    """)
    Optional<Member> findMemberWithDibsById(Long memberId);

    Optional<Member> findByNickname(String nickname);
}

