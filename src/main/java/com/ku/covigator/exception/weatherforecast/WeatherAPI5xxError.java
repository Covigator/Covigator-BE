package com.ku.covigator.exception.weatherforecast;

public class WeatherAPI5xxError extends WeatherForecastException {

    public WeatherAPI5xxError() {
        super(4003, "날씨 예보 API 요청 5xx 에러");
    }
}
