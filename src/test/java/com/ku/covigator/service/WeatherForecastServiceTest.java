package com.ku.covigator.service;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response;
import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.weather.WeatherProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.*;
import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.*;
import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.Items.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class WeatherForecastServiceTest {

    @Autowired
    WeatherForecastService weatherForecastService;
    @MockBean
    private WeatherProvider weatherProvider;

    @DisplayName("단기 예보 조회 결과를 반환한다.")
    @Test
    void requestShortTermWeatherForecast() {
        //given
        ShortTermWeatherForecastResponse response =
                new ShortTermWeatherForecastResponse(
                        new Response(
                                new Body(
                                        new Items(
                                                List.of(
                                                        new Item("PTY", "20241011", "1300", "1"),
                                                        new Item("SKY", "20241012", "1400", "1")
                                                )
                                        )
                                )
                        )
                );

        given(weatherProvider.requestWeatherForecast(anyInt(), anyInt())).willReturn(response);

        //when
        WeatherForecastResponse forecastResponse = weatherForecastService.getWeatherForecastInfo("20241011", 37.5157305555555f, 126.856397222222f);

        //then
        assertThat(forecastResponse.result()).isEqualTo("비");

    }

    @DisplayName("단기 예보 조회에 실패한다.")
    @Test
    void requestShortTermWeatherForecastFail() {
        //given
        given(weatherProvider.requestWeatherForecast(anyInt(), anyInt())).willReturn(null);

        //when
        WeatherForecastResponse forecastResponse = weatherForecastService.getWeatherForecastInfo("20241011", 37.5157305555555f, 126.856397222222f);

        //then
        assertThat(forecastResponse.result()).isEqualTo("날씨 정보를 찾을 수 없습니다.");
    }

}