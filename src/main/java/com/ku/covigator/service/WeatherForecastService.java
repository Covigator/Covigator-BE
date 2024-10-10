package com.ku.covigator.service;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.exception.weatherforecast.WeatherAPI4xxError;
import com.ku.covigator.exception.weatherforecast.WeatherAPI5xxError;
import com.ku.covigator.support.weather.Grid;
import com.ku.covigator.support.weather.WeatherCoordinateConverter;
import com.ku.covigator.support.weather.WeatherForecastAnalyzer;
import com.ku.covigator.support.weather.WeatherForecastUriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static com.ku.covigator.support.weather.WeatherCondition.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherForecastUriBuilder uriBuilder;
    private final RestClient weatherRestClient;
    private final WeatherForecastAnalyzer analyzer;
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
        Grid grid = WeatherCoordinateConverter.convertToGrid(longitude, latitude);

        URI uri = uriBuilder.buildWeatherForecastRequestUri(grid.getNx(), grid.getNy());
        ShortTermWeatherForecastResponse forecastResponse = requestWeatherForecast(uri);

        if (forecastResponse == null) {
            return WeatherForecastResponse.of(NO_WEATHER_INFO.getDescription());
        }

        List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> items = filterItemsWithDateAndCategory(date, forecastResponse);

        String result = analyzer.analyzeWeatherForecastResult(items);
        return WeatherForecastResponse.of(result);
    }

    private ShortTermWeatherForecastResponse requestWeatherForecast(URI uri) {
        return weatherRestClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new WeatherAPI4xxError();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new WeatherAPI5xxError();
                })
                .body(ShortTermWeatherForecastResponse.class);
    }

    private List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> filterItemsWithDateAndCategory(String date, ShortTermWeatherForecastResponse forecastResponse) {
        return forecastResponse.getItem().stream()
                .filter(item -> date.equals(item.getFcstDate()))
                .filter(item -> CODE_SKY.equals(item.getCategory()) || CODE_PRECIPITATION.equals(item.getCategory()))
                .toList();
    }

}
