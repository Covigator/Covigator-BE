package com.ku.covigator.dto.response;

import com.ku.covigator.domain.member.Member;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record SaveMessageResponse(String nickname, String message, String timestamp, Long memberId, String profileImageUrl) {

    public static SaveMessageResponse from(Member member, String message) {
        return SaveMessageResponse.builder()
                .nickname(member.getNickname())
                .message(message)
                .timestamp(String.valueOf(new Timestamp(System.currentTimeMillis())))
                .memberId(member.getId())
                .profileImageUrl(member.getImageUrl())
                .build();
    }
}
