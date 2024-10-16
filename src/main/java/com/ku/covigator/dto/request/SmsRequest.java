package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SmsRequest(
        @Pattern(regexp = "^01\\d{9}$",
                message = "올바른 휴대폰 번호 형식이 아닙니다.") String phoneNumber) {
}
