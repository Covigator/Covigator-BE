package com.ku.covigator.security.jwt;

import com.ku.covigator.config.properties.JwtProperties;
import com.ku.covigator.exception.jwt.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String principal) {
        Date now = new Date();
        Date expiresIn = new Date(now.getTime() + jwtProperties.getExpirationLength());

        return Jwts.builder()
                .subject(principal)
                .issuedAt(now)
                .expiration(expiresIn)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidException();
        } catch (MalformedJwtException e) {
            throw new JwtMalformedTokenException();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedTokenException();
        }
        return true;
    }

    public String getTokenFromRequestHeader(String requestHeader) {
        if(requestHeader == null) {
            throw new JwtNotFoundException();
        }
        if(!requestHeader.startsWith(JWT_TOKEN_PREFIX)) {
            throw new JwtUnsupportedTokenException();
        }
        return requestHeader.substring(JWT_TOKEN_PREFIX.length());
    }

    public String getPrincipal(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidException();
        }
    }
}
