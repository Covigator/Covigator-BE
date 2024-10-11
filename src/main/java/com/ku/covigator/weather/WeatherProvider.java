package com.ku.covigator.weather;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import com.ku.covigator.exception.weatherforecast.WeatherAPI4xxError;
import com.ku.covigator.exception.weatherforecast.WeatherAPI5xxError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class WeatherProvider {

    private final RestClient weatherRestClient;
    private final WeatherForecastUriBuilder uriBuilder;

    public ShortTermWeatherForecastResponse requestWeatherForecast(int nx, int ny) {
        URI uri = uriBuilder.buildWeatherForecastRequestUri(nx, ny);
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
}
