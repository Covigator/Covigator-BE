package com.ku.covigator.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("aws")
public class AwsProperties {
    private final String accessKey;
    private final String secretKey;
    private final String region;
}
