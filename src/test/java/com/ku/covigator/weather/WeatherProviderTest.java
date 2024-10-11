package com.ku.covigator.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ku.covigator.config.properties.WeatherForecastProperties;
import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.exception.weatherforecast.WeatherAPI4xxError;
import com.ku.covigator.exception.weatherforecast.WeatherAPI5xxError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.*;
import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.*;
import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.*;
import static com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.Items.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(value = {WeatherProvider.class, WeatherForecastUriBuilder.class, WeatherRestClientConfig.class})
class WeatherProviderTest {

    @Autowired
    WeatherProvider weatherProvider;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockRestServiceServer server;
    @Autowired
    WeatherForecastProperties weatherForecastProperties;

    private static final String WEATHER_FORECAST_URI = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    @BeforeEach
    void setup() {
        server.reset();
    }

    @DisplayName("단기 예보 조회를 요청한다.")
    @Test
    void requestShortTermWeatherForecast() throws JsonProcessingException {
        //given
        ShortTermWeatherForecastResponse response =
                new ShortTermWeatherForecastResponse(
                        new Response(
                                new Body(
                                        new Items(
                                                List.of(
                                                        new Item("PTY", "20241011", "1300", "1"),
                                                        new Item("SKY", "20241012", "1400", "0")
                                                )
                                        )
                                )
                        )
                );

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String baseDate = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = BaseTimeMapper.mapToBaseTime(now.toLocalTime());
        int nx = 55;
        int ny = 57;

        server.expect(requestTo(WEATHER_FORECAST_URI +
                        "?ServiceKey=" + weatherForecastProperties.getServiceKey() +
                        "&pageNo=1&numOfRows=1000&dataType=JSON" +
                        "&base_date=" + baseDate +
                        "&base_time=" + baseTime +
                        "&nx=" + nx +
                        "&ny=" + ny))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        //when
        ShortTermWeatherForecastResponse forecastResponse = weatherProvider.requestWeatherForecast(nx, ny);

        //then
        assertAll(
                () -> assertThat(forecastResponse.getItem().get(0).getCategory()).isEqualTo("PTY"),
                () -> assertThat(forecastResponse.getItem().get(1).getCategory()).isEqualTo("SKY"),
                () -> assertThat(forecastResponse.getItem().get(0).getFcstDate()).isEqualTo("20241011"),
                () -> assertThat(forecastResponse.getItem().get(1).getFcstDate()).isEqualTo("20241012"),
                () -> assertThat(forecastResponse.getItem().get(0).getFcstTime()).isEqualTo("1300"),
                () -> assertThat(forecastResponse.getItem().get(1).getFcstTime()).isEqualTo("1400"),
                () -> assertThat(forecastResponse.getItem().get(0).getFcstValue()).isEqualTo("1"),
                () -> assertThat(forecastResponse.getItem().get(1).getFcstValue()).isEqualTo("0")
        );
    }

    @DisplayName("기상청 서버 에러 발생 시 단기 예보 조회 요청에 실패한다.")
    @Test
    void requestShortTermWeatherForecastFailsWhenServerThrowsInternalServerException() {
        //given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String baseDate = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = BaseTimeMapper.mapToBaseTime(now.toLocalTime());
        int nx = 55;
        int ny = 57;

        server.expect(requestTo(WEATHER_FORECAST_URI +
                        "?ServiceKey=" + weatherForecastProperties.getServiceKey() +
                        "&pageNo=1&numOfRows=1000&dataType=JSON" +
                        "&base_date=" + baseDate +
                        "&base_time=" + baseTime +
                        "&nx=" + nx +
                        "&ny=" + ny))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        //when //then
        assertThatThrownBy(() -> weatherProvider.requestWeatherForecast(nx, ny))
                .isInstanceOf(WeatherAPI5xxError.class);
    }


    @DisplayName("기상청 API 잘못된 요청 시 단기 예보 조회 요청에 실패한다.")
    @Test
    void requestShortTermWeatherForecastFailsWhenServerThrows4xxException() {
        //given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String baseDate = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = BaseTimeMapper.mapToBaseTime(now.toLocalTime());
        int nx = 55;
        int ny = 57;

        server.expect(requestTo(WEATHER_FORECAST_URI +
                        "?ServiceKey=" + weatherForecastProperties.getServiceKey() +
                        "&pageNo=1&numOfRows=1000&dataType=JSON" +
                        "&base_date=" + baseDate +
                        "&base_time=" + baseTime +
                        "&nx=" + nx +
                        "&ny=" + ny))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        //when //then
        assertThatThrownBy(() -> weatherProvider.requestWeatherForecast(nx, ny))
                .isInstanceOf(WeatherAPI4xxError.class);
    }

}