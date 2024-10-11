package com.ku.covigator.weather;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.Items.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WeatherForecastAnalyzerTest {

    @Autowired
    private WeatherForecastAnalyzer analyzer;

    @DisplayName("단기 예보 조회 (1) - 비")
    @Test
    void returnRain() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "1"),
                new Item("PTY", "20241011", "1400", "2"),
                new Item("PTY", "20241011", "1500", "3")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("비");
    }

    @DisplayName("단기 예보 조회 (2) - 비/눈")
    @Test
    void returnRainSnow() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "2"),
                new Item("PTY", "20241011", "1400", "1"),
                new Item("PTY", "20241011", "1500", "3")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("비/눈");
    }

    @DisplayName("단기 예보 조회 (3) - 눈")
    @Test
    void returnSnow() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "3"),
                new Item("PTY", "20241011", "1400", "1"),
                new Item("PTY", "20241011", "1500", "2")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("눈");
    }

    @DisplayName("단기 예보 조회 (4) - 맑음")
    @Test
    void returnSunny() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "0"),
                new Item("SKY", "20241011", "1300", "1")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("맑음");
    }

    @DisplayName("단기 예보 조회 (5) - 구름 많음")
    @Test
    void returnPartlyCloudy() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "0"),
                new Item("SKY", "20241011", "1300", "3")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("구름 많음");
    }

    @DisplayName("단기 예보 조회 (5) - 흐림")
    @Test
    void returnCloudy() {
        //given
        List<Item> item = List.of(
                new Item("PTY", "20241011", "1300", "0"),
                new Item("SKY", "20241011", "1300", "4")
        );

        //when
        String result = analyzer.analyzeWeatherForecastResult(item);

        //then
        assertThat(result).isEqualTo("흐림");
    }


}