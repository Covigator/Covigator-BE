package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoSignInResponse(String accessToken, String isNew) {

    public static KakaoSignInResponse fromNewMember(String accessToken) {
        return new KakaoSignInResponse(accessToken, "True");
    }

    public static KakaoSignInResponse fromOldMember(String accessToken) {
        return new KakaoSignInResponse(accessToken, "False");
    }

}
