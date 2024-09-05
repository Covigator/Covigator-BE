package com.ku.covigator.controller;

import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support")
@WebMvcTest(controllers = LikeController.class)
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    LikeService likeService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("좋아요 클릭 요청")
    @Test
    void likeCourse() throws Exception {
        //given
        Long courseId = 1L;

        //when //then
        mockMvc.perform(post("/community/courses/{course_id}/likes", courseId))
                .andDo(print())
                .andExpect(status().isOk());
    }

}