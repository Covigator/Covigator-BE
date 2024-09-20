package com.ku.covigator.controller;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.dto.response.GetChatHistoryResponse;
import com.ku.covigator.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Chat", description = "채팅")
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 기록 조회")
    @GetMapping("/chat/{course_id}")
    public ResponseEntity<GetChatHistoryResponse> getChatHistory(@PathVariable(value = "course_id") Long courseId) {
        List<Chat> chatList = chatService.getChatHistory(courseId);
        return ResponseEntity.ok(GetChatHistoryResponse.from(chatList));
    }

}
