package com.ku.covigator.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotNull(message = "NULL일 수 없습니다.")
        @Size(min = 1, max = 300, message = "글자 길이는 1~300자여야 합니다.") String message) {

}
