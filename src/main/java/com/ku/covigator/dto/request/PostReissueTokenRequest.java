package com.ku.covigator.dto.request;

import jakarta.validation.constraints.NotNull;

public record PostReissueTokenRequest(

        @NotNull(message = "NULL일 수 없습니다.") String refreshToken) {
}
