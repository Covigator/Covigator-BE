package com.covigator.Covigator.security.jwt;

import com.covigator.Covigator.config.properties.JwtProperties;
import com.covigator.Covigator.exception.jwt.JwtExpiredException;
import com.covigator.Covigator.exception.jwt.JwtInvalidException;
import com.covigator.Covigator.exception.jwt.JwtMalformedTokenException;
import com.covigator.Covigator.exception.jwt.JwtUnsupportedTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

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
