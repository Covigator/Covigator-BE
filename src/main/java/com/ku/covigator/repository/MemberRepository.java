package com.ku.covigator.repository;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPlatform(String email, Platform platform);
}

