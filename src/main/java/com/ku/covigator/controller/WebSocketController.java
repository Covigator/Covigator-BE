package com.ku.covigator.controller;

import com.ku.covigator.dto.request.ChatMessageRequest;
import com.ku.covigator.dto.response.SaveMessageResponse;
import com.ku.covigator.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessageSendingOperations simpleMessageSendingOperations;
    private final ChatService chatService;

    @MessageMapping("/chat/{course_id}")
    @SendTo("/topic/chat/{course_id}")
    public void sendMessage(@DestinationVariable(value = "course_id") Long courseId,
                            SimpMessageHeaderAccessor accessor,
                            @Payload @Valid ChatMessageRequest request) {
        Long memberId = Long.parseLong(Objects.requireNonNull(accessor.getSessionAttributes()).get("memberId").toString());
        SaveMessageResponse saveMessageResponse = chatService.saveMessage(memberId, courseId, request.message());
        simpleMessageSendingOperations.convertAndSend("/topic/chat/" + courseId, saveMessageResponse);
    }
}
