package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TokenResponse(String accessToken, String refreshToken) {

    public static TokenResponse from(final String accessToken, final String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}
