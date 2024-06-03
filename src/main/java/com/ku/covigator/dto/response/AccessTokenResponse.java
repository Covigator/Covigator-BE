package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AccessTokenResponse(String accessToken) {

    public static AccessTokenResponse from(final String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
