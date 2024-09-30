package com.ku.covigator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostVerifyNicknameRequest(
        @NotBlank(message = "공백일 수 없습니다.")
        @Size(min = 1, max = 10, message = "글자 길이는 1~10자여야 합니다.") String nickname) {
}
