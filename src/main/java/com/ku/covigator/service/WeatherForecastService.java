package com.ku.covigator.service;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse.Response.Body.Items.Item;
import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.weather.*;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ku.covigator.weather.WeatherCondition.*;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherProvider provider;
    private final WeatherForecastAnalyzer analyzer;
    private final WeatherCoordinateConverter converter;
    private static final String CODE_SKY = "SKY";
    private static final String CODE_PRECIPITATION = "PTY";

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3, // 3회 시도
            backoff = @Backoff(delay = 500) // 재시도 시 0.5초 후 시도
    )
    public WeatherForecastResponse getWeatherForecastInfo(String date, float latitude, float longitude) {

        // GPS -> 격자 변환
        Grid grid = converter.convertToGrid(longitude, latitude);

        ShortTermWeatherForecastResponse forecastResponse = provider.requestWeatherForecast(grid.getNx(), grid.getNy());

        if (forecastResponse == null) {
            return WeatherForecastResponse.of(NO_WEATHER_INFO.getDescription());
        }

        List<Item> items = filterItemsWithDateAndCategory(date, forecastResponse);

        String result = analyzer.analyzeWeatherForecastResult(items);
        return WeatherForecastResponse.of(result);
    }

    private List<Item> filterItemsWithDateAndCategory(String date, ShortTermWeatherForecastResponse forecastResponse) {
        return forecastResponse.getItem().stream()
                .filter(item -> date.equals(item.getFcstDate()))
                .filter(item -> CODE_SKY.equals(item.getCategory()) || CODE_PRECIPITATION.equals(item.getCategory()))
                .toList();
    }

}
