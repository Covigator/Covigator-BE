package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.request.PatchMemberRequest;
import com.ku.covigator.dto.request.PostVerifyNicknameRequest;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support.slack")
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    private JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("회원 정보 수정을 요청한다.")
    @Test
    void updateMemberInfo() throws Exception {
        //given
        PatchMemberRequest request = PatchMemberRequest.builder()
                .nickname("covi")
                .password("covigator123!")
                .passwordVerification("covigator123!")
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);

        //when //then
        mockMvc.perform(patch("/members")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않는 경우 상태코드 400을 반환한다.")
    @Test
    void return400WhenPasswordIsNotEqualToPasswordVerification() throws Exception {
        //given
        PatchMemberRequest request = PatchMemberRequest.builder()
                .nickname("covi")
                .password("covigator123!")
                .passwordVerification("covi123!")
                .build();

        //when //then
        mockMvc.perform(patch("/members", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("닉네임 중복 확인을 요청한다.")
    @Test
    void verifyNicknameDuplication() throws Exception {
        //given
        PostVerifyNicknameRequest request = new PostVerifyNicknameRequest("covi");

        //when //then
        mockMvc.perform(post("/members/check-for-duplicate/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원 삭제를 요청한다.")
    @Test
    void deleteMember() throws Exception {
        //given
        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(1L);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);

        //when //then
        mockMvc.perform(delete("/members")
                ).andDo(print())
                .andExpect(status().isOk());
    }

}