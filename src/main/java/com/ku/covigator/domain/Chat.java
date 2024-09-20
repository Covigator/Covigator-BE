package com.ku.covigator.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    private String id;
    private Long courseId;
    private String timestamp;
    private String nickname;
    private String message;

    @Builder
    public Chat(Long courseId, String timestamp, String nickname, String message) {
        this.courseId = courseId;
        this.timestamp = timestamp;
        this.nickname = nickname;
        this.message = message;
    }
}
