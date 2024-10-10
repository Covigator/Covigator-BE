package com.ku.covigator.exception.weatherforecast;

public class WeatherAPI4xxError extends WeatherForecastException{

    public WeatherAPI4xxError() {
        super(4002, "날씨 예보 API 요청 4xx 에러");
    }
}
