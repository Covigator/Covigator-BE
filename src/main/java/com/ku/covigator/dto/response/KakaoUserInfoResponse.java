package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(KakaoAccount kakaoAccount){

    public record KakaoAccount(String email, Profile profile) {
        public record Profile (String thumbnail_image_url) { }

    }

    public Member toEntity() {
        return Member.builder()
                .email(kakaoAccount.email)
                .imageUrl(kakaoAccount.profile.thumbnail_image_url)
                .password(null)
                .platform(Platform.KAKAO)
                .build();
    }

}
