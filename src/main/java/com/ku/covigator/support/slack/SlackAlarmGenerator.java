package com.ku.covigator.support.slack;

import com.ku.covigator.config.properties.SlackProperties;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Component
@EnableConfigurationProperties(SlackProperties.class)
@RequiredArgsConstructor
public class SlackAlarmGenerator {

    private final SlackProperties slackProperties;
    private final Slack slack = Slack.getInstance();

    @Async
    public void sendSlackAlertErrorLog(Exception e, HttpServletRequest request) {

        RequestInfo requestInfo = RequestInfo.from(request);

        try {
            slack.send(slackProperties.getUrl(), payload(p -> p
                    .text("[서버 에러 발생]")
                    .attachments(
                            List.of(generateSlackAttachment(e, requestInfo))
                    )
            ));
        } catch (IOException slackError) {
            log.error("Slack 통신 예외 발생");
        }
    }

    private Attachment generateSlackAttachment(Exception e, RequestInfo request) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader();
        return Attachment.builder()
                .color("ff0000")
                .title(requestTime + " 발생 에러 로그")
                .fields(List.of(
                                generateSlackField("Request IP", xffHeader == null ? request.getRemoteAddr() : xffHeader),
                                generateSlackField("Request URL", request.getRequestURL() + " " + request.getMethod()),
                                generateSlackField("Error Message", e.getMessage())
                        )
                )
                .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

}
