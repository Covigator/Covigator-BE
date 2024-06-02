package com.ku.covigator.security.jwt;

import com.ku.covigator.exception.jwt.JwtNotFoundException;
import com.ku.covigator.exception.jwt.JwtUnsupportedTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = jwtProvider.getTokenFromRequestHeader(requestHeader);
        return jwtProvider.validateToken(accessToken);

    }

}
