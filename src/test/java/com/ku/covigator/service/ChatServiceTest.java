package com.ku.covigator.service;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.dto.response.SaveMessageResponse;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.ChatRepository;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        courseRepository.deleteAllInBatch();
        chatRepository.deleteAll();
        memberRepository.deleteAllInBatch();
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

    @DisplayName("채팅 메시지를 저장한다.")
    @Test
    void saveMessage() {
        //given
        Member member = Member.builder()
                .platform(Platform.KAKAO)
                .nickname("김코비")
                .email("covi@naver.com")
                .build();
        Member savedMember = memberRepository.save(member);

        Course course = Course.builder()
                .name("건대 풀코스")
                .isPublic('Y')
                .description("건대 핫플 리스트")
                .build();
        Course savedCourse = courseRepository.save(course);

        //when
        SaveMessageResponse response = chatService.saveMessage(savedMember.getId(), savedCourse.getId(), "여기 좋아요");

        //then
        Assertions.assertAll(
                () -> assertThat(response.message()).isEqualTo("여기 좋아요"),
                () -> assertThat(response.nickname()).isEqualTo("김코비"),
                () -> assertThat(response.memberId()).isEqualTo(savedMember.getId()),
                () -> assertThat(response.profileImageUrl()).isEqualTo(savedMember.getImageUrl())
        );
    }

    @DisplayName("존재하지 않는 회원에 대한 채팅 메시지 저장은 예외를 발생시킨다.")
    @Test
    void saveMessageFailsWhenMemberNotFound() {

        //when //then
        assertThatThrownBy(() -> chatService.saveMessage(100L, 100L, ""))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("존재하지 않는 코스에 대한 채팅 메시지 저장은 예외를 발생시킨다.")
    @Test
    void saveMessageFailsWhenCourseNotFound() {
        Member member = Member.builder()
                .platform(Platform.KAKAO)
                .nickname("김코비")
                .email("covi@naver.com")
                .build();
        Member savedMember = memberRepository.save(member);

        //when //then
        assertThatThrownBy(() -> chatService.saveMessage(savedMember.getId(), 100L, ""))
                .isInstanceOf(NotFoundCourseException.class);
    }

}