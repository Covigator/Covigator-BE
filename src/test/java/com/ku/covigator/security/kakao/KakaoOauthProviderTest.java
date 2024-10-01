package com.ku.covigator.security.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.response.KakaoTokenResponse;
import com.ku.covigator.dto.response.KakaoUserInfoResponse;
import com.ku.covigator.exception.oauth.KakaoMemberInfoRequestException;
import com.ku.covigator.exception.oauth.KakaoServerException;
import com.ku.covigator.exception.oauth.KakaoTokenRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(value = {KakaoOauthProvider.class, KakaoRestClientConfig.class, KakaoUriBuilder.class})
class KakaoOauthProviderTest {

    @Autowired
    KakaoOauthProvider kakaoOauthProvider;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockRestServiceServer server;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_TOKEN_PATH = "/oauth/token";

    @BeforeEach
    void setup() {
        server.reset();
    }

    @DisplayName("사용자 정보 조회를 요청한다.")
    @Test
    void getUserInfo() throws JsonProcessingException {
        //given
        String accessToken = "accesstokenaccesstokenaccesstoken";
        String kakaoEmail = "covi@naver.com";

        KakaoUserInfoResponse response =
                new KakaoUserInfoResponse(
                        new KakaoUserInfoResponse.KakaoAccount(kakaoEmail,
                                new KakaoUserInfoResponse.KakaoAccount.Profile("image")));

        server.expect(requestTo(KAKAO_USER_INFO_URL))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BEARER " + accessToken))
                .andRespond(withSuccess(objectMapper.writeValueAsBytes(response), MediaType.APPLICATION_JSON));

        //when
        KakaoUserInfoResponse kakaoUserInfo = kakaoOauthProvider.getKakaoUserInfo(accessToken);

        //then
        assertThat(kakaoUserInfo.kakaoAccount().email()).isEqualTo(kakaoEmail);
    }

    @DisplayName("잘못된 토큰으로 사용자 정보 조회시 예외가 발생한다.")
    @Test
    void requestUserInfoWithInvalidTokenOccursException() {
        //given
        String accessToken = " ";

        server.expect(requestTo(KAKAO_USER_INFO_URL))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BEARER " + accessToken))
                .andRespond(withBadRequest());

        //when //then
        assertThatThrownBy(() -> kakaoOauthProvider.getKakaoUserInfo(accessToken))
                .isInstanceOf(KakaoMemberInfoRequestException.class);
    }

    @DisplayName("카카오 서버에 에러가 발생한 경우 사용자 정보 조회 요청이 실패한다.")
    @Test
    void requestUserInfoFailsWhenKakaoServerOccursInternalServerException() {
        //given
        String accessToken = "accesstokenaccesstokenaccesstoken";

        server.expect(requestTo(KAKAO_USER_INFO_URL))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "BEARER " + accessToken))
                .andRespond(withServerError());

        //when //then
        assertThatThrownBy(() -> kakaoOauthProvider.getKakaoUserInfo(accessToken))
                .isInstanceOf(KakaoServerException.class);
    }

    @DisplayName("토큰 발급을 요청에 성공한다.")
    @Test
    void getAccessToken() throws JsonProcessingException {
        //given
        String accessToken = "accesstokenaccesstokenaccesstoken";
        Integer expiresIn = 86400;
        String code = "code";

        KakaoTokenResponse response = new KakaoTokenResponse(accessToken, expiresIn);

        server.expect(request -> Assertions.assertEquals(KAKAO_TOKEN_PATH, request.getURI().getPath()))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsBytes(response), MediaType.APPLICATION_JSON));

        //when
        KakaoTokenResponse tokenResponse = kakaoOauthProvider.getKakaoToken(code);

        //then
        assertThat(tokenResponse).usingRecursiveComparison().isEqualTo(response);
    }

    @DisplayName("카카오 서버에 에러가 발생한 경우 토큰 발급 요청에 실패한다.")
    @Test
    void requestAccessTokenFailsWhenKakaoServerOccursInternalServerException() {
        //given
        server.expect(request -> Assertions.assertEquals(KAKAO_TOKEN_PATH, request.getURI().getPath()))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        //when //then
        assertThatThrownBy(() -> kakaoOauthProvider.getKakaoToken("code"))
                .isInstanceOf(KakaoServerException.class);
    }

    @DisplayName("잘못된 인가 코드로 토큰 반환 요청시 예외가 발생한다.")
    @Test
    void requestTokenWithInvalidCodeOccursException() {
        //given
        server.expect(request -> Assertions.assertEquals(KAKAO_TOKEN_PATH, request.getURI().getPath()))
                .andExpect(content().contentType("application/x-www-form-urlencoded"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        //when //then
        assertThatThrownBy(() -> kakaoOauthProvider.getKakaoToken("code"))
                .isInstanceOf(KakaoTokenRequestException.class);
    }
}