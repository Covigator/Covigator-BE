package com.ku.covigator.service;

import com.ku.covigator.domain.Member;
import com.ku.covigator.exception.badrequest.DuplicateMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String signUp(Member member) {

        // 회원 가입 중복 검증
        validateDuplicateMember(member);

        // 패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.encodePassword(encodedPassword);

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        // 토큰 반환
        return jwtProvider.createToken(savedMember.getId().toString());
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> savedMember = memberRepository.findByEmail(member.getEmail());
        if(savedMember.isPresent()) {
            throw new DuplicateMemberException();
        }
    }
}
