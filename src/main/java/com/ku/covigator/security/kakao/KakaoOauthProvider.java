package com.ku.covigator.security.kakao;

import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.exception.oauth.KakaoMemberInfoRequestException;
import com.ku.covigator.exception.oauth.KakaoServerException;
import com.ku.covigator.exception.oauth.KakaoTokenRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class KakaoOauthProvider {

    private final RestClient restClient;

    private final KakaoUriBuilder uriBuilder;

    private static final String BEARER_PREFIX = "BEARER ";

    // 카카오 토큰 반환
    public KakaoTokenResponse getKakaoToken(String code) {
        URI uri = uriBuilder.buildKakaoTokenRequestUri(code);
        return requestTokenToKakaoServer(restClient, uri);
    }

    // 카카오 사용자 정보 반환
    public KakaoUserInfoResponse getKakaoUserInfo(String token) {
        URI uri = uriBuilder.buildKakaoUserInfoRequestUri();
        return requestUserInfoToKakaoServer(token, restClient, uri);
    }

    private KakaoTokenResponse requestTokenToKakaoServer(RestClient restClient, URI uri) {
        return restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new KakaoTokenRequestException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new KakaoServerException();
                })
                .body(KakaoTokenResponse.class);
    }

    private KakaoUserInfoResponse requestUserInfoToKakaoServer(String token, RestClient restClient, URI uri) {
        return restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new KakaoMemberInfoRequestException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new KakaoServerException();
                })
                .body(KakaoUserInfoResponse.class);
    }

}
