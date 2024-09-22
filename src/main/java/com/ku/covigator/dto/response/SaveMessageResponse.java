package com.ku.covigator.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record SaveMessageResponse(String sender, String message, String timestamp) {

    public static SaveMessageResponse of(String sender, String message) {
        return SaveMessageResponse.builder()
                .sender(sender)
                .message(message)
                .timestamp(String.valueOf(new Timestamp(System.currentTimeMillis())))
                .build();
    }
}
