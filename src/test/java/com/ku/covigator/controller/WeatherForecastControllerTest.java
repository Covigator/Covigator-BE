package com.ku.covigator.controller;

import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.WeatherForecastService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan("com.ku.covigator.support.slack")
@WebMvcTest(controllers = WeatherForecastController.class)
class WeatherForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WeatherForecastService weatherForecastService;
    @MockBean
    private JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    private JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("날씨 정보 조회를 요청한다.")
    @Test
    void requestWeatherInfo() throws Exception {
        //given
        WeatherForecastResponse response = new WeatherForecastResponse("맑음");

        given(weatherForecastService.getWeatherForecastInfo("20241010", 127.123f, 57.123f)).willReturn(response);

        //when //then
        mockMvc.perform(get("/weather-forecast")
                        .queryParam("date", "20241010")
                        .queryParam("latitude", "127.123")
                        .queryParam("longitude", "57.123")
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.result").value("맑음")
                );
    }

}