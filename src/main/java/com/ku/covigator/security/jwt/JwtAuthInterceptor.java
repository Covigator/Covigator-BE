package com.ku.covigator.security.jwt;

import com.ku.covigator.exception.jwt.JwtNotFoundException;
import com.ku.covigator.exception.jwt.JwtUnsupportedTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (validatePreflight(request)) {
            return true;
        }

        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = jwtProvider.getTokenFromRequestHeader(requestHeader);
        return jwtProvider.validateToken(accessToken);

    }

    public boolean validatePreflight(HttpServletRequest request) {

        return StringUtils.equals(request.getMethod(), "OPTIONS");
    }

}
