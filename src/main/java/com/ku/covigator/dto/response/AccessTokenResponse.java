package com.ku.covigator.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenResponse {

    private String accessToken;

    public static AccessTokenResponse from(final String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
