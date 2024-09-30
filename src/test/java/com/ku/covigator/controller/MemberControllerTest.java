package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.request.PatchMemberRequest;
import com.ku.covigator.dto.request.PostVerifyNicknameRequest;
import com.ku.covigator.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan({"com.ku.covigator.support", "com.ku.covigator.security.jwt"})
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;

    @DisplayName("회원 정보 수정을 요청한다.")
    @Test
    void updateMemberInfo() throws Exception {
        //given
        PatchMemberRequest request = PatchMemberRequest.builder()
                .nickname("covi")
                .password("covigator123!")
                .passwordVerification("covigator123!")
                .build();

        //when //then
        mockMvc.perform(patch("/members/{member_id}", 1L)
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
        mockMvc.perform(patch("/members/{member_id}", 1L)
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
        mockMvc.perform(post("/members/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

}