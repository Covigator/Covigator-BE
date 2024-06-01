package com.ku.covigator.security.jwt;

import com.ku.covigator.config.properties.JwtProperties;
import com.ku.covigator.exception.jwt.JwtExpiredException;
import com.ku.covigator.exception.jwt.JwtInvalidException;
import com.ku.covigator.exception.jwt.JwtMalformedTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtProviderTest {

    @Autowired private JwtProvider jwtProvider;

    @DisplayName("유효한 토큰을 생성한다.")
    @Test
    void createToken() {
        //given
        String principal = "covi@naver.com";

        //when
        String token = jwtProvider.createToken(principal);

        //then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @DisplayName("올바른 토큰 정보로 payload를 조회한다.")
    @Test
    void getPayload() {
        //given
        String email = "covi@naver.com";
        String token = jwtProvider.createToken(email);

        //when
        String principal = jwtProvider.getPrincipal(token);

        //then
        assertThat(principal).isEqualTo(email);
    }

    @DisplayName("유효하지 않은 토큰 형식으로 payload 를 조회할 경우 예외를 발생시킨다.")
    @Test
    void getPayloadByInvalidToken() {
        //when //then
        assertThatThrownBy(() -> jwtProvider.getPrincipal(null))
                .isInstanceOf(JwtInvalidException.class);
    }

    @DisplayName("만료된 토큰으로 payload 를 조회할 경우 예외를 발생시킨다.")
    @Test
    void getPayloadByExpiredToken() throws InterruptedException {
        //given
        String jwtSecretKey = "covicovicovicovicovicovicovicovicovicovicovicovicovicovicovicovi";
        JwtProperties jwtProperties = new JwtProperties(jwtSecretKey, 0L);
        JwtProvider customJwtProvider = new JwtProvider(jwtProperties);

        //when
        String token = customJwtProvider.createToken("covi@naver.com");

        //then
        assertThatThrownBy(() -> customJwtProvider.getPrincipal(token))
                .isInstanceOf(JwtExpiredException.class);
    }

    @DisplayName("위/변조된 토큰 검증 시 예외가 발생한다.")
    @Test
    void malformedTokenThrowsException() {
        //given
        String malformedToken = "invalidToken";

        //when //then
        assertThatThrownBy(() -> jwtProvider.validateToken(malformedToken))
                .isInstanceOf(JwtMalformedTokenException.class);
    }

    @DisplayName("유효하지 않은 토큰 검증 시 예외가 발생한다.")
    @Test
    void invalidTokenThrowsException() {
        //when //then
        assertThatThrownBy(() -> jwtProvider.validateToken(null))
                .isInstanceOf(JwtInvalidException.class);
    }

    @DisplayName("만료된 토큰 검증 시 예외가 발생한다.")
    @Test
    void expiredTokenThrowsException() throws InterruptedException {
        //given
        String jwtSecretKey = "covicovicovicovicovicovicovicovicovicovicovicovicovicovicovicovi";
        JwtProperties jwtProperties = new JwtProperties(jwtSecretKey, 0L);
        JwtProvider customJwtProvider = new JwtProvider(jwtProperties);

        //when
        String token = customJwtProvider.createToken("covi@naver.com");

        //then
        assertThatThrownBy(() -> customJwtProvider.validateToken(token))
                .isInstanceOf(JwtExpiredException.class);
    }

}