package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostSignUpRequest(
        @NotBlank(message = "공백일 수 없습니다.") String nickname,
        @Email String email,
        @NotBlank(message = "공백일 수 없습니다.") String password) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .platform(Platform.LOCAL)
                .build();
    }
}
