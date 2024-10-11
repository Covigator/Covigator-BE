package com.ku.covigator.weather;

import com.ku.covigator.exception.weatherforecast.BaseTimeMappingException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BaseTimeMapper {

    // 단기 예보 발표 시각에 맞게 기준 시각 변환
    public static String mapToBaseTime(LocalTime currentTime) {

        LocalTime baseTime = null;

        if (currentTime.isAfter(LocalTime.of(22, 59)) || currentTime.isBefore(LocalTime.of(2, 0))) {
            baseTime = LocalTime.of(23, 0);
        } else if (currentTime.isAfter(LocalTime.of(1, 59)) && currentTime.isBefore(LocalTime.of(5, 0))) {
            baseTime = LocalTime.of(2, 0);
        } else if (currentTime.isAfter(LocalTime.of(4, 59)) && currentTime.isBefore(LocalTime.of(8, 0))) {
            baseTime = LocalTime.of(5, 0);
        } else if (currentTime.isAfter(LocalTime.of(7, 59)) && currentTime.isBefore(LocalTime.of(11, 0))) {
            baseTime = LocalTime.of(8, 0);
        } else if (currentTime.isAfter(LocalTime.of(10, 59)) && currentTime.isBefore(LocalTime.of(14, 0))) {
            baseTime = LocalTime.of(11, 0);
        } else if (currentTime.isAfter(LocalTime.of(13, 59)) && currentTime.isBefore(LocalTime.of(17, 0))) {
            baseTime = LocalTime.of(14, 0);
        } else if (currentTime.isAfter(LocalTime.of(16, 59)) && currentTime.isBefore(LocalTime.of(20, 0))) {
            baseTime = LocalTime.of(17, 0);
        } else if (currentTime.isAfter(LocalTime.of(19, 59)) && currentTime.isBefore(LocalTime.of(23, 0))) {
            baseTime = LocalTime.of(20, 0);
        }

        if (baseTime == null) {
            throw new BaseTimeMappingException();
        }

        return baseTime.format(DateTimeFormatter.ofPattern("HHmm"));

    }

}
