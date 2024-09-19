package com.ku.covigator.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.ku.covigator.config.properties.AwsProperties;
import com.ku.covigator.config.properties.S3Properties;
import io.findify.s3mock.S3Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@EnableConfigurationProperties(AwsProperties.class)
public class MockS3Config {

    private final AwsProperties awsProperties;
    private final S3Properties s3Properties;

    public MockS3Config(AwsProperties awsProperties, S3Properties s3Properties) {
        this.awsProperties = awsProperties;
        this.s3Properties = s3Properties;
    }

    @Bean
    public S3Mock s3Mock() {
        return new S3Mock.Builder().withPort(8080).withInMemoryBackend().build();
    }

    @Bean
    public AmazonS3 amazonS3(S3Mock s3Mock) {

        s3Mock.start();
        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8080", awsProperties.getRegion());
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        client.createBucket(s3Properties.getBucket());
        return client;
    }
}
