package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.exception.badrequest.DuplicateMemberNicknameException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateMember(Long memberId, String nickname, String password) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        // 닉네임 중복 검증
        verifyNicknameDuplication(nickname);

        // 패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(password);
        member.updateMemberInfo(nickname, encodedPassword);

        // 회원 정보 수정
        memberRepository.save(member);
    }

    public void verifyNicknameDuplication(String nickname) {
        if(memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateMemberNicknameException();
        }
    }
}
