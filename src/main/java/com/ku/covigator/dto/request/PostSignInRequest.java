package com.ku.covigator.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostSignInRequest(
        @NotBlank(message = "공백일 수 없습니다.") String email,
        @NotBlank(message = "공백일 수 없습니다.") String password
) {

}
