package com.ku.covigator.dto.request;

import com.ku.covigator.domain.Member;
import lombok.Builder;

@Builder
public record PostSignUpRequest(String imageUrl, String name, String nickname, String email, String password) {
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .imageUrl(imageUrl)
                .build();
    }
}
