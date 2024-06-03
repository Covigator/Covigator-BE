package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostSignUpRequest(String imageUrl, String name, String nickname, String email, String password) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .imageUrl(imageUrl)
                .platform(Platform.LOCAL)
                .build();
    }
}
