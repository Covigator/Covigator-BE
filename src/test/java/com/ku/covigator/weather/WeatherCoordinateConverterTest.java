package com.ku.covigator.weather;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WeatherCoordinateConverterTest {

    @Autowired
    private WeatherCoordinateConverter converter;

    @DisplayName("위경도를 격자로 변환한다.")
    @Test
    void convertToGrid() {
        //given
        float latitude = 37.5635694444444f;
        float longitude = 126.980008333333f;

        //when
        Grid grid = converter.convertToGrid(longitude, latitude);

        //then
        Assertions.assertThat(grid.getNx()).isEqualTo(60);
        Assertions.assertThat(grid.getNy()).isEqualTo(127);
    }
}