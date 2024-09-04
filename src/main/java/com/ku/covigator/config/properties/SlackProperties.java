package com.ku.covigator.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("slack.webhook.url")
public final class SlackProperties {
    private final String slackWebhookUrl;
}
