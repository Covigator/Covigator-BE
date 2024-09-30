package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PatchMemberRequest(
        @NotBlank(message = "공백일 수 없습니다.")
        @Size(min = 1, max = 10, message = "글자 길이는 1~10자여야 합니다.") String nickname,
        @Pattern(regexp = "^(?=.*[A-Za-z가-힣])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-=\\[\\]{};':\",./<>?\\\\|`]).{7,15}$",
                message = "한글/영문, 숫자, 특수문자를 포함하여 7~15자를 입력해주세요.") String password,
        @Pattern(regexp = "^(?=.*[A-Za-z가-힣])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-=\\[\\]{};':\",./<>?\\\\|`]).{7,15}$",
                message = "한글/영문, 숫자, 특수문자를 포함하여 7~15자를 입력해주세요.") String passwordVerification) {

}
