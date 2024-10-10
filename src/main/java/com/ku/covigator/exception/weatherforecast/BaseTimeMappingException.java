package com.ku.covigator.exception.weatherforecast;

public class BaseTimeMappingException extends WeatherForecastException{

    public BaseTimeMappingException() {
        super(4001, "올바른 기준 시각을 찾을 수 없습니다.");
    }
}
