package com.ku.covigator.service;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.domain.Course;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.repository.ChatRepository;
import com.ku.covigator.repository.CourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private CourseRepository courseRepository;

    @AfterEach
    void tearDown() {
        courseRepository.deleteAllInBatch();
        chatRepository.deleteAll();
    }

    @DisplayName("채팅 기록을 오래된 순으로 조회한다.")
    @Test
    void getChatHistory() {
        //given
        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .build();
        Course savedCourse = courseRepository.save(course);

        String time = new Timestamp(System.currentTimeMillis()).toString();
        Chat chat = Chat.builder()
                .courseId(savedCourse.getId())
                .timestamp(time)
                .nickname("김코비")
                .message("여기 좋아요")
                .build();

        String time2 = new Timestamp(System.currentTimeMillis()).toString();
        Chat chat2 = Chat.builder()
                .courseId(savedCourse.getId())
                .timestamp(time2)
                .nickname("박코비")
                .message("저는 별로,,")
                .build();
        chatRepository.saveAll(List.of(chat, chat2));

        //when
        List<Chat> chatList = chatService.getChatHistory(savedCourse.getId());

        //then
        assertAll(
                () -> assertThat(chatList).hasSize(2),
                () -> assertThat(chatList)
                        .extracting("nickname")
                        .containsExactly("김코비", "박코비"),
                () -> assertThat(chatList)
                        .extracting("message")
                        .containsExactly("여기 좋아요", "저는 별로,,")
        );
    }

    @DisplayName("존재하지 않는 코스에 대한 채팅 조회는 예외를 발생시킨다.")
    @Test
    void getChatHistoryFailsWhenNotFoundCourse() {
        //given

        //when //then
        assertThatThrownBy(() -> chatService.getChatHistory(100L))
                .isInstanceOf(NotFoundCourseException.class);
    }

}