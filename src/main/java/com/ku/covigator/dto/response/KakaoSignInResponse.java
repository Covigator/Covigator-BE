package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoSignInResponse(String accessToken, String isNew, String refreshToken) {

    public static KakaoSignInResponse fromNewMember(String accessToken, String refreshToken) {
        return new KakaoSignInResponse(accessToken, "True", refreshToken);
    }

    public static KakaoSignInResponse fromOldMember(String accessToken, String refreshToken) {
        return new KakaoSignInResponse(accessToken, "False", refreshToken);
    }

}
