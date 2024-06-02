package com.ku.covigator.dto.response;

public record AccessTokenResponse(String accessToken) {

    public static AccessTokenResponse from(final String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
