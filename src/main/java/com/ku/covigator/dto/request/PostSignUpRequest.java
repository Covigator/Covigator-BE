package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostSignUpRequest(
        @NotBlank(message = "공백일 수 없습니다.")
        @Size(min = 1, max = 10, message = "글자 길이는 1~10자여야 합니다.") String nickname,
        @Email(message = "올바른 이메일 형식이 아닙니다.") String email,
        @Pattern(regexp = "^(?=.*[A-Za-z가-힣])(?=.*\\d)(?=.*[!@#$%^&*()_+~\\-=\\[\\]{};':\",./<>?\\\\|`]).{7,15}$",
                message = "한글/영문, 숫자, 특수문자를 포함하여 7~15자를 입력해주세요.") String password) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .platform(Platform.LOCAL)
                .build();
    }
}
