package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SignUpResponse(String accessToken, String refreshToken, String nickname, String email, String imageUrl) {

    public static SignUpResponse from(final String accessToken, final String refreshToken, final String nickname, final String email, final String imageUrl) {
        return new SignUpResponse(accessToken, refreshToken, nickname, email, imageUrl);
    }
}
