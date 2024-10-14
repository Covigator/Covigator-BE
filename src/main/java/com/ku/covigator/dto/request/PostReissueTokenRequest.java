package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostReissueTokenRequest(
        @NotNull(message = "NULL일 수 없습니다.") String refreshToken) {
}
