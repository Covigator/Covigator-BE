package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostSignUpRequest;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.dto.request.PostSignInRequest;
import com.ku.covigator.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@ComponentScan("com.ku.covigator.support")
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("로그인을 요청한다.")
    @Test
    void signIn() throws Exception {
        //given
        String email = "covi@naver.com";
        String password = "covigator123";
        String token = "token";
        PostSignInRequest request = new PostSignInRequest("covi@naver.com", "covigator123");

        given(authService.signIn(email, password)).willReturn(token);

        //when //then
        mockMvc.perform(post("/accounts/sign-in")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(token));
    }

    @DisplayName("회원 가입한다.")
    @Test
    void signUp() throws Exception {
        //given
        PostSignUpRequest request = PostSignUpRequest.builder()
                .email("www.covi.com")
                .nickname("covi")
                .imageUrl("covi@naver.com")
                .imageUrl("covigator123")
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

        given(authService.signUp(any(), any())).willReturn("token");

        //when //then
        mockMvc.perform(multipart("/accounts/sign-up")
                        .file(imageFile)
                        .file(jsonRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"));
    }

    @DisplayName("신규 회원에 대한 카카오 로그인을 요청한다.")
    @Test
    void signInKakaoNewMember() throws Exception {
        //given
        KakaoSignInResponse response = KakaoSignInResponse.fromNewMember("token");
        given(authService.signInKakao("code")).willReturn(response);

        //when //then
        mockMvc.perform(get("/accounts/oauth/kakao")
                        .param("code", "code")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value("token"),
                        jsonPath("$.is_new").value("True")
                );
    }

    @DisplayName("기존 회원에 대한 카카오 로그인을 요청한다.")
    @Test
    void signInKakaoOldMember() throws Exception {
        //given
        KakaoSignInResponse response = KakaoSignInResponse.fromOldMember("token");
        given(authService.signInKakao("code")).willReturn(response);

        //when //then
        mockMvc.perform(get("/accounts/oauth/kakao")
                        .param("code", "code")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.access_token").value("token"),
                        jsonPath("$.is_new").value("False")
                );
    }

}