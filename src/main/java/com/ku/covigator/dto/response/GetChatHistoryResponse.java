package com.ku.covigator.dto.response;

import com.ku.covigator.domain.Chat;
import lombok.Builder;

import java.util.List;

public record GetChatHistoryResponse(Long myId, List<ChatDto> chat) {

    @Builder
    public record ChatDto(String nickname, String timestamp, String message, String profileImageUrl, Long memberId) {
    }

    public static GetChatHistoryResponse from(Long myId, List<Chat> chatList) {

        List<ChatDto> chatDtos = chatList.stream()
                .map(chat -> ChatDto.builder()
                        .nickname(chat.getNickname())
                        .message(chat.getMessage())
                        .timestamp(chat.getTimestamp())
                        .profileImageUrl(chat.getProfileImageUrl())
                        .memberId(chat.getMemberId())
                        .build()
                ).toList();

        return new GetChatHistoryResponse(myId, chatDtos);
    }
}
