package com.ku.covigator.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("weather")
public class WeatherForecastProperties {
    private final String serviceKey;
}
