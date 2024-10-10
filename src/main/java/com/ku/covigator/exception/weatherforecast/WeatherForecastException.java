package com.ku.covigator.exception.weatherforecast;

import com.ku.covigator.exception.CovigatorException;
import org.springframework.http.HttpStatus;

public class WeatherForecastException extends CovigatorException {

    public WeatherForecastException(int code, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, code, message);
    }
}
