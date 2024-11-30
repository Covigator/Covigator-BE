package com.ku.covigator.security.kakao;

import com.ku.covigator.config.properties.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoUriBuilder {

    private final KakaoProperties kakaoProperties;
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String REDIRECTION_URI = "redirect_uri";
    private static final String CODE = "code";
    private static final String AUTHORIZATION_CODE = "authorization_code";

    public URI buildKakaoTokenRequestUri(String code) {
        return UriComponentsBuilder.fromUriString(kakaoProperties.getTokenUrl())
                .queryParam(GRANT_TYPE, AUTHORIZATION_CODE)
                .queryParam(CLIENT_ID, kakaoProperties.getClientId())
                .queryParam(REDIRECTION_URI, kakaoProperties.getRedirectUri())
                .queryParam(CODE, code)
                .build(true)
                .toUri();
    }

    public URI buildKakaoUserInfoRequestUri() {
        return UriComponentsBuilder.fromUriString(kakaoProperties.getUserInfoUrl()).build().toUri();
    }

}
