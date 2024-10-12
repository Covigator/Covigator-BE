package com.ku.covigator.weather;

import org.springframework.stereotype.Component;

import static java.lang.Math.*;

@Component
public class WeatherCoordinateConverter {

    private static final float RE = 6371.00877f; // 사용할 지구반경 [km]
    private static final float GRID = 5.0f; // 격자간격 [km]
    private static final float SLAT1 = 30.0f; // 표준위도 [degree]
    private static final float SLAT2 = 60.0f; // 표준위도 [degree]
    private static final float OLON = 126.0f; // 기준점의 경도 [degree]
    private static final float OLAT = 38.0f; // 기준점의 위도 [degree]
    private static final float XO = 43; // 기준점의 X좌표
    private static final float YO = 136; // 기준점의 Y좌표


    // 위경도를 X,Y 좌표로 변환하는 메서드
    public Grid convertToGrid(float longitude, float latitude) {

        double re = RE / GRID;
        double slat1Rad = SLAT1 * (PI / 180.0);
        double slat2Rad = SLAT2 * (PI / 180.0);
        double olonRad = OLON * (PI / 180.0);
        double olatRad = OLAT * (PI / 180.0);
        double sn = tan(PI * 0.25 + slat2Rad * 0.5) / tan(PI * 0.25 + slat1Rad * 0.5);
        sn = log(cos(slat1Rad) / cos(slat2Rad)) / log(sn);
        double sf = tan(PI * 0.25 + slat1Rad * 0.5);
        sf = pow(sf, sn) * cos(slat1Rad) / sn;
        double ro = tan(PI * 0.25 + olatRad * 0.5);
        ro = re * sf / pow(ro, sn);

        // 위경도 -> X,Y 변환
        double ra = tan(PI * 0.25 + (latitude * (PI / 180.0)) * 0.5);
        ra = re * sf / pow(ra, sn);
        double theta = longitude * (PI / 180.0) - olonRad;
        if (theta > PI) theta -= 2.0 * PI;
        if (theta < -PI) theta += 2.0 * PI;
        theta *= sn;

        int x = (int) floor(ra * sin(theta) + XO + 0.5);
        int y = (int) floor(ro - ra * cos(theta) + YO + 0.5);

        return new Grid(x, y);
    }

}