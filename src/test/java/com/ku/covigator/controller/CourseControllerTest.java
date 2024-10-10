package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.dto.request.PostCourseRequest;
import com.ku.covigator.dto.response.GetCommunityCourseInfoResponse;
import com.ku.covigator.dto.response.GetCommunityCourseListResponse;
import com.ku.covigator.dto.response.GetDibsCourseListResponse;
import com.ku.covigator.dto.response.GetMyCourseListResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support.slack")
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

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );
        MockMultipartFile imageFile2 = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "postCourseRequest",
                null,
                "application/json",
                objectMapper.writeValueAsBytes(postCourseRequest)
        );

        //when //then
        mockMvc.perform(multipart("/community/courses")
                        .file(imageFile)
                        .file(imageFile2)
                        .file(jsonRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("커뮤니티 코스 리스트를 조회한다.")
    @Test
    void getAllCommunityCourses() throws Exception {
        //given
        Long memberId = 1L;

        GetCommunityCourseListResponse.CourseList courseDto = GetCommunityCourseListResponse.CourseList.builder()
                .courseId(1L)
                .name("건대 풀코스")
                .description("건대 핫플 요약 코스")
                .score(5.0)
                .dibs(true)
                .imageUrl("www.imageUrl.com")
                .build();

        GetCommunityCourseListResponse.CourseList courseDto2 = GetCommunityCourseListResponse.CourseList.builder()
                .courseId(2L)
                .name("건대 풀코스2")
                .description("건대 핫플 요약 코스2")
                .score(0.0)
                .dibs(false)
                .imageUrl("www.imageUrl2.com")
                .build();

        GetCommunityCourseListResponse response = GetCommunityCourseListResponse.builder()
                .courses(List.of(courseDto, courseDto2))
                .hasNext(false)
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(memberId);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);

        given(courseService.findAllCourses(any(), any())).willReturn(response);
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
                        jsonPath("$.courses[0].course_id").value(1L),
                        jsonPath("$.courses[0].name").value("건대 풀코스"),
                        jsonPath("$.courses[0].description").value("건대 핫플 요약 코스"),
                        jsonPath("$.courses[0].score").value(5.0),
                        jsonPath("$.courses[0].dibs").value(true),
                        jsonPath("$.courses[0].image_url").value("www.imageUrl.com"),
                        jsonPath("$.courses[1].course_id").value(2L),
                        jsonPath("$.courses[1].name").value("건대 풀코스2"),
                        jsonPath("$.courses[1].description").value("건대 핫플 요약 코스2"),
                        jsonPath("$.courses[1].score").value(0.0),
                        jsonPath("$.courses[1].dibs").value(false),
                        jsonPath("$.courses[1].image_url").value("www.imageUrl2.com"),
                        jsonPath("$.has_next").value(false)
                );
    }

    @DisplayName("커뮤니티 코스 상세 정보를 조회한다.")
    @Test
    void getCommunityCourseInfo() throws Exception {
        //given
        Long memberId = 1L;
        Long courseId = 1L;

        GetCommunityCourseInfoResponse.PlaceList placeDto = GetCommunityCourseInfoResponse.PlaceList.builder()
                .placeName("가츠시")
                .placeDescription("공대생 추천 맛집")
                .imageUrl("www.image.com")
                .category("식당")
                .build();

        GetCommunityCourseInfoResponse response = GetCommunityCourseInfoResponse.builder()
                .courseName("건대 풀코스")
                .courseDescription("건대 핫플 요약 코스")
                .dibsCnt(100L)
                .dibs(true)
                .places(List.of(placeDto))
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(memberId);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);
        given(courseService.findCourse(memberId, courseId)).willReturn(response);

        //when //then
        mockMvc.perform(get("/community/courses/{course_id}", courseId))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.course_name").value("건대 풀코스"),
                        jsonPath("$.course_description").value("건대 핫플 요약 코스"),
                        jsonPath("$.dibs_cnt").value("100"),
                        jsonPath("$.dibs").value(true),
                        jsonPath("$.places[0].place_name").value("가츠시"),
                        jsonPath("$.places[0].place_description").value("공대생 추천 맛집"),
                        jsonPath("$.places[0].category").value("식당"),
                        jsonPath("$.places[0].image_url").value("www.image.com")
                );
    }

    @DisplayName("코스 삭제 요청한다.")
    @Test
    void deleteCommunityCourse() throws Exception {
        //given
        Long courseId = 1L;

        //when //then
        mockMvc.perform(delete("/community/courses/{course_id}", courseId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("찜한 코스 모아보기를 요청한다.")
    @Test
    void getLikedCourses() throws Exception {
        //given
        Long memberId = 1L;

        GetDibsCourseListResponse.DibsCourseList courseDto = GetDibsCourseListResponse.DibsCourseList.builder()
                .name("건대 풀코스")
                .description("건대 핫플 요약 코스")
                .score(5.0)
                .imageUrl("www.image.com")
                .build();

        GetDibsCourseListResponse response = GetDibsCourseListResponse.builder()
                .courses(List.of(courseDto))
                .hasNext(false)
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(memberId);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);
        given(courseService.findLikedCourses(memberId)).willReturn(response);

        //when //then
        mockMvc.perform(get("/my-page/dibs-courses"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.courses[0].name").value("건대 풀코스"),
                        jsonPath("$.courses[0].description").value("건대 핫플 요약 코스"),
                        jsonPath("$.courses[0].score").value(5.0),
                        jsonPath("$.courses[0].image_url").value("www.image.com"),
                        jsonPath("$.has_next").value(false)
                );
    }

    @DisplayName("마이 코스 모아보기를 요청한다.")
    @Test
    void getMyCourses() throws Exception {
        //given
        Long memberId = 1L;

        GetMyCourseListResponse.MyCourseList courseDto = GetMyCourseListResponse.MyCourseList.builder()
                .name("건대 풀코스")
                .description("건대 핫플 요약 코스")
                .score(5.0)
                .imageUrl("www.image.com")
                .isPublic('Y')
                .build();

        GetMyCourseListResponse response = GetMyCourseListResponse.builder()
                .courses(List.of(courseDto))
                .hasNext(false)
                .build();

        given(jwtAuthArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(memberId);
        given(jwtAuthArgumentResolver.supportsParameter(any())).willReturn(true);
        given(courseService.findMyCourses(memberId)).willReturn(response);

        //when //then
        mockMvc.perform(get("/my-page/my-courses"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.courses[0].name").value("건대 풀코스"),
                        jsonPath("$.courses[0].description").value("건대 핫플 요약 코스"),
                        jsonPath("$.courses[0].score").value(5.0),
                        jsonPath("$.courses[0].image_url").value("www.image.com"),
                        jsonPath("$.courses[0].is_public").value("Y"),
                        jsonPath("$.has_next").value(false)
                );
    }
}