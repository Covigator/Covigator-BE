package com.ku.covigator.dto.response;

import com.ku.covigator.domain.member.Member;
import lombok.Builder;

@Builder
public record SaveMessageResponse(String nickname, String message, String time, Long memberId, String profileImageUrl) {

    public static SaveMessageResponse from(Member member, String message, String time) {
        return SaveMessageResponse.builder()
                .nickname(member.getNickname())
                .message(message)
                .time(time)
                .memberId(member.getId())
                .profileImageUrl(member.getImageUrl())
                .build();
    }
}
