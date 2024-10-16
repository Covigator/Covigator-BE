package com.ku.covigator.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record VerifyEmailCodeRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다.") String email,
        @NotEmpty(message = "공백일 수 없습니다.") String code ) {
}
