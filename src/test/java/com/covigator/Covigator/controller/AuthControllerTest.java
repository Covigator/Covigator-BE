package com.covigator.Covigator.controller;

import com.covigator.Covigator.common.interceptor.JwtAuthInterceptor;
import com.covigator.Covigator.dto.request.MemberSignInRequest;
import com.covigator.Covigator.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtAuthInterceptor jwtAuthInterceptor;

    @DisplayName("로그인을 요청한다.")
    @WithMockUser(username = "test")
    @Test
    void signIn() throws Exception {
        //given
        MemberSignInRequest request = new MemberSignInRequest("covi@naver.com", "covigator123");

        //when //then
        mockMvc.perform(post("/accounts/sign-in")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }
}