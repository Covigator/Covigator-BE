package com.ku.covigator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SnsClient snsClient;

    private static final String PHONE_CODE_KOREA = "+82";

    @Async
    public void sendSms(String phoneNumber, String message) {

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(PHONE_CODE_KOREA + phoneNumber)
                    .build();

            PublishResponse result = snsClient.publish(request);
        } catch (Exception e) {
            log.error("send sms error: {}", e.getMessage());
        }

    }
}
