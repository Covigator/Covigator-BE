package com.ku.covigator.service;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.weather.*;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

import static com.ku.covigator.weather.WeatherCondition.*;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherForecastUriBuilder uriBuilder;
    private final WeatherProvider provider;
    private final WeatherForecastAnalyzer analyzer;
    private final WeatherCoordinateConverter converter;
    private final static String CODE_SKY = "SKY";
    private final static String CODE_PRECIPITATION = "PTY";

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3, // 3회 시도
            backoff = @Backoff(delay = 500), // 재시도 시 0.5초 후 시도
            recover = "recover"
    )
    public WeatherForecastResponse getWeatherForecastInfo(String date, float latitude, float longitude) {

        // GPS -> 격자 변환
        Grid grid = converter.convertToGrid(longitude, latitude);

        URI uri = uriBuilder.buildWeatherForecastRequestUri(grid.getNx(), grid.getNy());
        ShortTermWeatherForecastResponse forecastResponse = provider.requestWeatherForecast(uri);

        if (forecastResponse == null) {
            return WeatherForecastResponse.of(NO_WEATHER_INFO.getDescription());
        }

        List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> items = filterItemsWithDateAndCategory(date, forecastResponse);

        String result = analyzer.analyzeWeatherForecastResult(items);
        return WeatherForecastResponse.of(result);
    }

    private List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> filterItemsWithDateAndCategory(String date, ShortTermWeatherForecastResponse forecastResponse) {
        return forecastResponse.getItem().stream()
                .filter(item -> date.equals(item.getFcstDate()))
                .filter(item -> CODE_SKY.equals(item.getCategory()) || CODE_PRECIPITATION.equals(item.getCategory()))
                .toList();
    }

}
