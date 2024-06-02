package com.ku.covigator.service;

import com.ku.covigator.domain.Member;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public String signIn(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        validatePassword(password, member.getPassword());

        return jwtProvider.createToken(member.getId().toString());

    }

    private void validatePassword(String password, String encodedPassword) {

        if(!passwordEncoder.matches(password, encodedPassword)) {
            throw new PasswordMismatchException();
        }

    }

}
