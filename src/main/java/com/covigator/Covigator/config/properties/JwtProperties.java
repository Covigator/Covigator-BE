package com.covigator.Covigator.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("security.jwt")
public final class JwtProperties {
    private final String secretKey;
    private final long expirationLength;
}
