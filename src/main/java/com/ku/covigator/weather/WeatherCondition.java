package com.ku.covigator.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeatherCondition {

    RAIN("비"),
    RAIN_SNOW("비/눈"),
    SNOW("눈"),
    UNKNOWN_PRECIPITATION("알 수 없는 강수 형태"),
    SUNNY("맑음"),
    PARTLY_CLOUDY("구름 많음"),
    CLOUDY("흐림"),
    UNKNOWN_SKY("알 수 없는 하늘 상태"),
    NO_PRECIPITATION("강수 정보를 찾을 수 없습니다."),
    NO_WEATHER_INFO("날씨 정보를 찾을 수 없습니다.");

    private final String description;

}
