package com.ku.covigator.support.weather;

import com.ku.covigator.config.properties.WeatherForecastProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(WeatherForecastProperties.class)
public class WeatherForecastUriBuilder {

    private final WeatherForecastProperties weatherForecastProperties;
    private static final String weatherForecastUri = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final int PAGE_NO = 1;
    private static final int NUM_OF_ROWS = 1000;
    private static final String DATA_TYPE = "JSON";

    public URI buildWeatherForecastRequestUri(int nx, int ny) {

        // 오늘 날짜 구하기
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String baseDate = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = BaseTimeMapper.mapToBaseTime(now.toLocalTime());

        return UriComponentsBuilder.fromUriString(weatherForecastUri)
                .queryParam("ServiceKey", weatherForecastProperties.getServiceKey())
                .queryParam("pageNo", PAGE_NO)
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("dataType", DATA_TYPE)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();
    }

}
