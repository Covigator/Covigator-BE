package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReissueTokenResponse(String accessToken, String refreshToken) {

    public static ReissueTokenResponse from(final String accessToken, final String refreshToken) {
        return new ReissueTokenResponse(accessToken, refreshToken);
    }
}
