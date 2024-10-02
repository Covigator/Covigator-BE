package com.ku.covigator.dto.request;

import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @Pattern(regexp = "^(?=.*[A-Za-z가-힣])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-=\\[\\]{};':\",./<>?\\\\|`]).{7,15}$",
                message = "한글/영문, 숫자, 특수문자를 포함하여 7~15자를 입력해주세요.") String password,
        @Pattern(regexp = "^(?=.*[A-Za-z가-힣])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-=\\[\\]{};':\",./<>?\\\\|`]).{7,15}$",
                message = "한글/영문, 숫자, 특수문자를 포함하여 7~15자를 입력해주세요.") String passwordVerification) {
}
