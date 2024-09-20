package com.ku.covigator.service;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.repository.ChatRepository;
import com.ku.covigator.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final CourseRepository courseRepository;
    
    public List<Chat> getChatHistory(Long courseId) {

        courseRepository.findById(courseId).orElseThrow(NotFoundCourseException::new);
        return chatRepository.findChatByCourseIdOrderByTimestampAsc(courseId);
    }

}
