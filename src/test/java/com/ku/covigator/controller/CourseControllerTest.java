package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCourseListResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CourseService courseService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("코스를 등록한다.")
    @Test
    void addCourse() throws Exception {
        //given
        PostCourseRequest.PlaceDto placeDto = PostCourseRequest.PlaceDto.builder()
                .placeName("가츠시")
                .category("식당")
                .description("공대생 추천 맛집")
                .address("광진구")
                .build();

        PostCourseRequest.PlaceDto placeDto2 = PostCourseRequest.PlaceDto.builder()
                .placeName("레스티오")
                .category("카페")
                .description("공대생 추천 카페")
                .address("광진구")
                .build();

        PostCourseRequest postCourseRequest = PostCourseRequest.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .isPublic('Y')
                .places(List.of(placeDto, placeDto2))
                .build();

        //when //then
        mockMvc.perform(post("/community/courses")
                        .content(objectMapper.writeValueAsString(postCourseRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("커뮤니티 코스 리스트를 조회한다.")
    @Test
    void getAllCommunityCourses() throws Exception {
        //given
        GetCourseListResponse.CourseDto courseDto = GetCourseListResponse.CourseDto.builder()
                .name("건대 풀코스")
                .description("건대 핫플 요약 코스")
                .score(5.0)
                .build();

        GetCourseListResponse response = GetCourseListResponse.builder()
                .courses(List.of(courseDto))
                .hasNext(false)
                .build();

        given(courseService.findAllCourses(any())).willReturn(response);
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new CourseController(this.courseService)
                )
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        //when //then
        mockMvc.perform(get("/community/courses"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.courses[0].name").value("건대 풀코스"),
                        jsonPath("$.courses[0].description").value("건대 핫플 요약 코스"),
                        jsonPath("$.courses[0].score").value(5.0),
                        jsonPath("$.has_next").value(false)
                );
    }

    @DisplayName("커뮤니티 코스 상세 정보를 조회한다.")
    @Test
    void getCommunityCourseInfo() throws Exception {
        //given
        Long memberId = 1L;
        Long courseId = 1L;

        GetCommunityCourseInfoResponse.PlaceDto placeDto = GetCommunityCourseInfoResponse.PlaceDto.builder()
                .placeName("가츠시")
                .placeDescription("공대생 추천 맛집")
                .category("식당").build();

        GetCommunityCourseInfoResponse response = GetCommunityCourseInfoResponse.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .likeCnt(100L)
                .isLiked(true)
                .places(List.of(placeDto))
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(memberId);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);
        given(courseService.findCourse(memberId, courseId)).willReturn(response);

        //when //then
        mockMvc.perform(get("/community/courses/{courseId}", courseId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.course_name").value("건대 풀코스"),
                        jsonPath("$.course_description").value("건대 핫플 요약 코스"),
                        jsonPath("$.like_cnt").value("100"),
                        jsonPath("$.is_liked").value(true),
                        jsonPath("$.places[0].place_name").value("가츠시"),
                        jsonPath("$.places[0].place_description").value("공대생 추천 맛집"),
                        jsonPath("$.places[0].category").value("식당")
                );
    }
}