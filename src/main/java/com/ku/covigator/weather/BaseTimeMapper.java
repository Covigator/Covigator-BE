package com.ku.covigator.weather;

import com.ku.covigator.exception.weatherforecast.BaseTimeMappingException;
import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.NavigableMap;
import java.util.TreeMap;

@UtilityClass
public class BaseTimeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    private static final NavigableMap<LocalTime, LocalTime> BASE_TIME_MAP = new TreeMap<>();

    static {
        BASE_TIME_MAP.put(LocalTime.of(23, 0), LocalTime.of(23, 0));
        BASE_TIME_MAP.put(LocalTime.of(2, 0), LocalTime.of(2, 0));
        BASE_TIME_MAP.put(LocalTime.of(5, 0), LocalTime.of(5, 0));
        BASE_TIME_MAP.put(LocalTime.of(8, 0), LocalTime.of(8, 0));
        BASE_TIME_MAP.put(LocalTime.of(11, 0), LocalTime.of(11, 0));
        BASE_TIME_MAP.put(LocalTime.of(14, 0), LocalTime.of(14, 0));
        BASE_TIME_MAP.put(LocalTime.of(17, 0), LocalTime.of(17, 0));
        BASE_TIME_MAP.put(LocalTime.of(20, 0), LocalTime.of(20, 0));
    }

    // 단기 예보 발표 시각에 맞게 기준 시각 변환
    public static String mapToBaseTime(LocalTime currentTime) {

        if (currentTime.isBefore(LocalTime.of(2, 0))) {
            return LocalTime.of(23, 0).format(FORMATTER);
        }

        LocalTime baseTime = BASE_TIME_MAP.floorEntry(currentTime).getValue();

        if (baseTime == null) {
            throw new BaseTimeMappingException();
        }

        return baseTime.format(FORMATTER);
    }
}
