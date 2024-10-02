package com.ku.covigator.dto;

import com.amazonaws.services.simpleemail.model.*;
import lombok.Builder;

@Builder
public record EmailSenderDto(String receiver, String subject, String content) {

    private static final String FROM = "admin@covigator.shop";

    public SendEmailRequest toSendRequestDto() {

        Destination destination = new Destination().withToAddresses(receiver);
        Message message = new Message()
                .withSubject(createContent(subject))
                .withBody(new Body().withHtml(createContent(content)));
        return new SendEmailRequest()
                .withSource(FROM)
                .withDestination(destination)
                .withMessage(message);
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }
}
