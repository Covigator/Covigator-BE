package com.ku.covigator.weather;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BaseTimeMapperTest {

    @DisplayName("현재 시각을 기준 시각으로 변환한다.")
    @ParameterizedTest
    @MethodSource("provideArguments")
    void test(LocalTime currentTime, String expectedTime) {
        //when
        String baseTime = BaseTimeMapper.mapToBaseTime(currentTime);

        //then
        assertThat(baseTime).isEqualTo(expectedTime);
    }

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(LocalTime.of(1, 0), "2300"),
                Arguments.of(LocalTime.of(3, 0), "0200"),
                Arguments.of(LocalTime.of(6, 0), "0500"),
                Arguments.of(LocalTime.of(9, 0), "0800"),
                Arguments.of(LocalTime.of(12, 0), "1100"),
                Arguments.of(LocalTime.of(15, 0), "1400"),
                Arguments.of(LocalTime.of(18, 0), "1700"),
                Arguments.of(LocalTime.of(21, 0), "2000"),
                Arguments.of(LocalTime.of(23, 30), "2300")
        );
    }

}