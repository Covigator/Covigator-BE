package com.ku.covigator.service;

import com.ku.covigator.domain.Member;
import com.ku.covigator.exception.badrequest.DuplicateMemberException;
import com.ku.covigator.repository.MemberRepository;
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
            throw new DuplicateMemberException();
        }
    }
}
