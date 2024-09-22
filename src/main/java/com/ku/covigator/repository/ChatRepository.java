package com.ku.covigator.repository;

import com.ku.covigator.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, Long> {

    List<Chat> findChatByCourseIdOrderByTimestampAsc(Long courseId);
}
