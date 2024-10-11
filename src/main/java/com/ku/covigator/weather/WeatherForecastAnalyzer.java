package com.ku.covigator.weather;

import com.ku.covigator.dto.response.ShortTermWeatherForecastResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ku.covigator.weather.WeatherCondition.*;

@Component
public class WeatherForecastAnalyzer {

    public static final String PRECIPITATION = "PTY";
    public static final String SKY = "SKY";

    /**
     * 날씨 예보 분석 결과는 강수 유형 (PTY)이 우선권을 갖습니다. <br> <br>
     * 강수 유형이 존재하는 경우 가장 처음 발견되는 해당 강수 유형 (비, 비/눈, 눈)을 리턴합니다.<br>
     * ex) 하루 중 13시에 처음으로 "비" 예보가 있는 경우 "비" 리턴<br>
     * <br>
     * 강수 유형이 존재하지 않는 경우에는 하늘 상태 결과를 확인합니다.<br>
     * 하늘 상태는 해당 날짜에 가장 많이 발견되는 하늘 상태 결과를 리턴합니다.<br>
     * ex) 하루 중 "맑음"이 15번, "구름 많음"이 5번, "흐림"이 4번인 경우 "맑음" 리턴
     */
    public String analyzeWeatherForecastResult(List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> items) {
        // 1. 강수 유형 분석
        String precipitation = analyzePrecipitation(items);

        // 2. 강수 유형이 없을 경우 하늘 상태 분석
        return !precipitation.equals(NO_PRECIPITATION.getDescription()) ? precipitation : analyzeSkyCondition(items);
    }

    private String analyzePrecipitation(List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> items) {
        return items.stream()
                .filter(item -> PRECIPITATION.equals(item.getCategory()) && !item.getFcstValue().equals("0"))
                .map(this::getPrecipitationDescription)
                .findFirst()
                .orElse(NO_PRECIPITATION)
                .getDescription();
    }

    private String analyzeSkyCondition(List<ShortTermWeatherForecastResponse.Response.Body.Items.Item> items) {
        Map<String, Long> fcstValueCountMap = items.stream()
                .filter(item -> SKY.equals(item.getCategory()))
                .collect(Collectors.groupingBy(ShortTermWeatherForecastResponse.Response.Body.Items.Item::getFcstValue, Collectors.counting()));

        return fcstValueCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> getSkyConditionDescription(entry.getKey()))
                .orElse(NO_WEATHER_INFO)
                .getDescription();
    }

    private WeatherCondition getPrecipitationDescription(ShortTermWeatherForecastResponse.Response.Body.Items.Item item) {
        return switch (item.getFcstValue()) {
            case "1", "4" -> RAIN;
            case "2" -> RAIN_SNOW;
            case "3" -> SNOW;
            default -> UNKNOWN_PRECIPITATION;
        };
    }

    private WeatherCondition getSkyConditionDescription(String fcstValue) {
        return switch (fcstValue) {
            case "1" -> SUNNY;
            case "3" -> PARTLY_CLOUDY;
            case "4" -> CLOUDY;
            default -> UNKNOWN_SKY;
        };
    }
}