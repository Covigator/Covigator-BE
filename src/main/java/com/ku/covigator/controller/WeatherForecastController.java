package com.ku.covigator.controller;

import com.ku.covigator.dto.response.WeatherForecastResponse;
import com.ku.covigator.service.WeatherForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "weather forecast", description = "날씨 예보")
@RestController
@RequiredArgsConstructor
public class WeatherForecastController {

    private final WeatherForecastService weatherForecastService;

    @Operation(summary = "날씨 예보 조회")
    @GetMapping("/weather-forecast")
    public ResponseEntity<WeatherForecastResponse> getWeatherForecastInfo(
            @RequestParam String date,
            @RequestParam float latitude,
            @RequestParam float longitude) {
        return ResponseEntity.ok(weatherForecastService.getWeatherForecastInfo(date, latitude, longitude));
    }
}
