package com.ku.covigator.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("security.oauth.kakao")
public final class KakaoProperties {
    private final String clientId;
    private final String redirectUri;
    private final String userInfoUrl;
    private final String tokenUrl;
}
