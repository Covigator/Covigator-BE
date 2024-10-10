package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.request.PostReviewRequest;
import com.ku.covigator.dto.response.GetReviewResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support.slack")
@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ReviewService reviewService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("리뷰 등록을 요청한다.")
    @Test
    void addReviewRequests() throws Exception {
        //given
        Long courseId = 1L;
        PostReviewRequest request = PostReviewRequest.builder()
                .comment("좋아요~~~")
                .score(5)
                .build();

        //when //then
        mockMvc.perform(post("/community/courses/{course_id}/reviews", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("리뷰 조회를 요청한다.")
    @Test
    void test() throws Exception {
        //given
        Long courseId = 1L;
        GetReviewResponse.ReviewDto review = GetReviewResponse.ReviewDto.builder()
                .author("김코비")
                .comment("좋아요~~~")
                .score(5)
                .build();

        GetReviewResponse.ReviewDto review2 = GetReviewResponse.ReviewDto.builder()
                .author("박코비")
                .comment("싫어요")
                .score(1)
                .build();

        GetReviewResponse response = new GetReviewResponse(List.of(review, review2), false);

        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new ReviewController(this.reviewService)
                )
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        given(reviewService.findReviews(any(), any())).willReturn(response);

        //when //then
        mockMvc.perform(get("/community/courses/{course_id}/reviews", courseId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.has_next").value(false),
                        jsonPath("$.reviews[0].author").value("김코비"),
                        jsonPath("$.reviews[0].comment").value("좋아요~~~"),
                        jsonPath("$.reviews[0].score").value(5),
                        jsonPath("$.reviews[1].author").value("박코비"),
                        jsonPath("$.reviews[1].comment").value("싫어요"),
                        jsonPath("$.reviews[1].score").value(1)
                );
    }

}