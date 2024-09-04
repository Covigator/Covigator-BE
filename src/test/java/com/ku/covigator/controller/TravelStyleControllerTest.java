package com.ku.covigator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.domain.travelstyle.*;
import com.ku.covigator.dto.request.PatchTravelStyleRequest;
import com.ku.covigator.dto.request.PostTravelStyleRequest;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.TravelStyleService;
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

@ComponentScan("com.ku.covigator.support")
@WebMvcTest(controllers = TravelStyleController.class)
class TravelStyleControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    TravelStyleService travelStyleService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("여행 스타일 선택을 완료한다.")
    @Test
    void saveTravelStyle() throws Exception {
        //given
        PostTravelStyleRequest request = PostTravelStyleRequest.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();

        //when //then
        mockMvc.perform(post("/members/travel-styles")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("여행 스타일을 수정한다.")
    @Test
    void patchTravelStyle() throws Exception {
        //given
        PatchTravelStyleRequest request = PatchTravelStyleRequest.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();

        //when //then
        mockMvc.perform(patch("/members/travel-styles")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

    }

}