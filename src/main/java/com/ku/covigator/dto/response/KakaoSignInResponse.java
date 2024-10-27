package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoSignInResponse(String accessToken, String isNew, String refreshToken, String nickname, String email) {

    public static KakaoSignInResponse fromNewMember(String accessToken, String refreshToken, String nickname, String email) {
        return new KakaoSignInResponse(accessToken, "True", refreshToken, nickname, email);
    }

    public static KakaoSignInResponse fromOldMember(String accessToken, String refreshToken, String nickname, String email) {
        return new KakaoSignInResponse(accessToken, "False", refreshToken, nickname, email);
    }

}
