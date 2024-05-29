package com.covigator.Covigator.service;

import com.covigator.Covigator.domain.Member;
import com.covigator.Covigator.exception.DuplicateMemberException;
import com.covigator.Covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(Member member) {

        // 회원 가입 중복 검증
        validateDuplicateMember(member);

        // 패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.encodePassword(encodedPassword);

        // 회원 저장
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> savedMember = memberRepository.findByEmail(member.getEmail());
        if(savedMember.isPresent()) {
            throw new DuplicateMemberException("이미 가입된 사용자입니다.");
        }
    }
}
