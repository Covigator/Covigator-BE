package com.ku.covigator.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.ku.covigator.dto.EmailSenderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SESService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Async
    public void sendEmail(String subject, String content, String receiver) {

        EmailSenderDto senderDto = EmailSenderDto.builder()
                .receiver(receiver)
                .subject(subject)
                .content(content)
                .build();

        SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(senderDto.toSendRequestDto());

        if(sendEmailResult.getSdkHttpMetadata().getHttpStatusCode() != 200) {
            log.error("{}", sendEmailResult.getSdkResponseMetadata().toString());
        }
    }

}
