package com.ku.covigator.service;

import com.ku.covigator.domain.Chat;
import com.ku.covigator.domain.Course;
import com.ku.covigator.domain.member.Member;
import com.ku.covigator.dto.response.SaveMessageResponse;
import com.ku.covigator.exception.notfound.NotFoundCourseException;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.ChatRepository;
import com.ku.covigator.repository.CourseRepository;
import com.ku.covigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;

    public List<Chat> getChatHistory(Long courseId) {

        Course course = courseRepository.findById(courseId).orElseThrow(NotFoundCourseException::new);
        return chatRepository.findChatByCourseIdOrderByTimestampAsc(course.getId());
    }

    public SaveMessageResponse saveMessage(Long memberId, Long courseId, String message) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberException::new);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(NotFoundCourseException::new);

        Chat chat = buildChat(course.getId(), message, member);

        chatRepository.save(chat);

        return SaveMessageResponse.of(member.getNickname(), message);
    }

    private Chat buildChat(Long courseId, String message, Member member) {
        return Chat.builder()
                .message(message)
                .nickname(member.getNickname())
                .timestamp(String.valueOf(new Timestamp(System.currentTimeMillis())))
                .courseId(courseId)
                .build();
    }

}
