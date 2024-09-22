package com.ku.covigator.controller;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support")
@WebMvcTest(controllers = ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatService chatService;
    @MockBean
    private JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    private JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("채팅 기록 조회를 요청한다.")
    @Test
    void getChatHistory() throws Exception {
        //given
        Long courseId = 1L;
        List<Chat> chats = List.of(
                Chat.builder()
                        .message("안녕")
                        .nickname("김코비")
                        .build(),
                Chat.builder()
                        .message("안녕안녕")
                        .nickname("박코비")
                        .build()
        );

        given(chatService.getChatHistory(courseId)).willReturn(chats);

        //when //then
        mockMvc.perform(get("/chat/{course_id}", courseId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.chat[0].message").value("안녕"),
                        jsonPath("$.chat[1].message").value("안녕안녕"),
                        jsonPath("$.chat[0].nickname").value("김코비"),
                        jsonPath("$.chat[1].nickname").value("박코비")
                );
    }

}