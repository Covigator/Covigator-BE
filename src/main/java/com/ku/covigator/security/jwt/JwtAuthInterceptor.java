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
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = resolveAccessToken(request);
        return jwtProvider.validateToken(accessToken);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(accessToken == null) {
            throw new JwtNotFoundException();
        }
        if(!accessToken.startsWith(JWT_TOKEN_PREFIX)) {
            throw new JwtUnsupportedTokenException();
        }
        return accessToken.substring(JWT_TOKEN_PREFIX.length());
    }

}
