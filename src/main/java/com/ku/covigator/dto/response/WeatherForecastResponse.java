package com.ku.covigator.dto.response;

public record WeatherForecastResponse(String result) {

    public static WeatherForecastResponse of(String result) {
        return new WeatherForecastResponse(result);
    }
}
