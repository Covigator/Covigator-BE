package com.ku.covigator.dto.response;

import com.ku.covigator.domain.Chat;
import lombok.Builder;

import java.util.List;

public record GetChatHistoryResponse(List<ChatDto> chat) {

    @Builder
    public record ChatDto(String nickname, String timestamp, String message) {
    }

    public static GetChatHistoryResponse from(List<Chat> chatList) {

        List<ChatDto> chatDtos = chatList.stream()
                .map(chat -> ChatDto.builder()
                        .nickname(chat.getNickname())
                        .message(chat.getMessage())
                        .timestamp(chat.getTimestamp())
                        .build()
                ).toList();

        return new GetChatHistoryResponse(chatDtos);
    }
}
