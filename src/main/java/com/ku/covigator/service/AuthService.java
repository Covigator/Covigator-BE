package com.ku.covigator.service;

import com.ku.covigator.domain.Member;
import com.ku.covigator.exception.badrequest.PasswordMismatchException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String signIn(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        validatePassword(password, member.getPassword());

        return jwtProvider.createToken(member.getEmail());

    }

    private void validatePassword(String password, String encodedPassword) {

        if(!passwordEncoder.matches(password, encodedPassword)) {
            throw new PasswordMismatchException();
        }

    }

}
