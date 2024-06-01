package com.ku.covigator.dto.request;

import com.ku.covigator.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSignUpRequest {
    private String imageUrl;

    private String name;

    private String nickname;

    private String email;

    private String password;

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
