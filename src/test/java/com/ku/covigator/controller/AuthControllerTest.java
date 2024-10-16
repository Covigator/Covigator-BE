package com.ku.covigator.controller;

import com.ku.covigator.dto.request.*;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.dto.response.TokenResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.service.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support.slack")
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    private JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("로그인을 요청한다.")
    @Test
    void signIn() throws Exception {
        //given
        String email = "covi@naver.com";
        String password = "covigator123";
        TokenResponse response = new TokenResponse("access-token", "refresh-token");
        PostSignInRequest request = new PostSignInRequest("covi@naver.com", "covigator123");

        given(authService.signIn(email, password)).willReturn(response);

        //when //then
        mockMvc.perform(post("/accounts/sign-in")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(response.accessToken()))
                .andExpect(jsonPath("$.refresh_token").value(response.refreshToken()));
    }

    @DisplayName("회원 가입한다.")
    @Test
    void signUp() throws Exception {
        //given
        PostSignUpRequest request = PostSignUpRequest.builder()
                .email("covi123@naver.com")
                .nickname("covi")
                .password("covi123!@#")
                .build();

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "postSignUpRequest",
                null,
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        TokenResponse response = new TokenResponse("access-token", "refresh-token");

        given(authService.signUp(any(), any())).willReturn(response);

        //when //then
        mockMvc.perform(multipart("/accounts/sign-up")
                        .file(imageFile)
                        .file(jsonRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(response.accessToken()))
                .andExpect(jsonPath("$.refresh_token").value(response.refreshToken()));
    }

    @DisplayName("신규 회원에 대한 카카오 로그인을 요청한다.")
    @Test
    void signInKakaoNewMember() throws Exception {
        //given
        KakaoSignInResponse response = KakaoSignInResponse.fromNewMember("access-token", "refresh-token");
        given(authService.signInKakao("code")).willReturn(response);

        //when //then
        mockMvc.perform(get("/accounts/oauth/kakao")
                        .param("code", "code")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value("access-token"),
                        jsonPath("$.refresh_token").value("refresh-token"),
                        jsonPath("$.is_new").value("True")
                );
    }

    @DisplayName("기존 회원에 대한 카카오 로그인을 요청한다.")
    @Test
    void signInKakaoOldMember() throws Exception {
        //given
        KakaoSignInResponse response = KakaoSignInResponse.fromOldMember("access-token", "refresh-token");
        given(authService.signInKakao("code")).willReturn(response);

        //when //then
        mockMvc.perform(get("/accounts/oauth/kakao")
                        .param("code", "code")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value("access-token"),
                        jsonPath("$.refresh_token").value("refresh-token"),
                        jsonPath("$.is_new").value("False")
                );
    }

    @DisplayName("비밀번호 찾기를 요청한다.")
    @Test
    void findPassword() throws Exception {
        //given
        FindPasswordRequest request = new FindPasswordRequest("covi@naver.com");

        //when //then
        mockMvc.perform(post("/accounts/find-password/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @DisplayName("이메일 찾기 요청 - 문자 본인 확인")
    @Test
    void findEmail() throws Exception {
        //given
        SmsRequest request = new SmsRequest("01012341234");

        //when //then
        mockMvc.perform(post("/accounts/find-email/send-message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @DisplayName(" 이메일 인증번호 입력을 잘못하는 경우 상태코드 400을 반환한다.")
    @Test
    void writeWrongVerificationEmailCode() throws Exception {
        //given
        VerifyEmailCodeRequest request = new VerifyEmailCodeRequest("covi@naver.com", "abcd");
        BDDMockito.given(redisUtil.getData(any())).willReturn("");


        //when //then
        mockMvc.perform(post("/accounts/find-password/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isBadRequest()
                );
    }

    @DisplayName("본인확인 인증번호 입력을 잘못하는 경우 상태코드 400을 반환한다.")
    @Test
    void writeWrongVerificationSmsCode() throws Exception {
        //given
        VerifySmsCodeRequest request = new VerifySmsCodeRequest("abcd");
        BDDMockito.given(redisUtil.getData(any())).willReturn("");

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);

        //when //then
        mockMvc.perform(post("/accounts/find-email/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isBadRequest()
                );
    }

    @DisplayName("이메일 인증번호 입력에 성공한다.")
    @Test
    void writeRightVerificationEmailCode() throws Exception {
        //given
        VerifyEmailCodeRequest request = new VerifyEmailCodeRequest("covi@naver.com", "abcd");
        BDDMockito.given(redisUtil.getData(any())).willReturn("abcd");

        //when //then
        mockMvc.perform(post("/accounts/find-password/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @DisplayName("문자 본인확인 인증번호 입력에 성공한다.")
    @Test
    void writeRightVerificationSmsCode() throws Exception {
        //given
        VerifySmsCodeRequest request = new VerifySmsCodeRequest("abcd");
        BDDMockito.given(redisUtil.getData(any())).willReturn("abcd");

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);

        //when //then
        mockMvc.perform(post("/accounts/find-email/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @DisplayName("비밀번호 변경 시 비밀번호와 비밀번호 확인이 일치하지 않는 경우 상태코드 400을 반환한다.")
    @Test
    void passwordVerificationFailsInChangingPasswordRequest() throws Exception {
        //given
        ChangePasswordRequest request = new ChangePasswordRequest("covigator123!", "covi12!");

        //when //then
        mockMvc.perform(post("/accounts/change-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("비밀번호 변경을 요청한다.")
    @Test
    void changePassword() throws Exception {
        //given
        ChangePasswordRequest request = new ChangePasswordRequest("covigator123!", "covigator123!");

        //when //then
        mockMvc.perform(post("/accounts/change-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("리프레시/액세스 토큰을 재발급한다.")
    @Test
    void reissueToken() throws Exception {
        //given
        PostReissueTokenRequest request = new PostReissueTokenRequest("refreshtoken");
        TokenResponse response = new TokenResponse("access_token", "refresh_token");
        given(authService.reissueToken(any())).willReturn(response);

        //when //then
        mockMvc.perform(post("/accounts/reissue-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value(response.accessToken()),
                        jsonPath("$.refresh_token").value(response.refreshToken())
                );
    }

}