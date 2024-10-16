package com.ku.covigator.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record VerifySmsCodeRequest(@NotEmpty(message = "공백일 수 없습니다.") String code) {
}
