package co.com.pragma.sqs.listener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "listener.sqs")
public record SQSProperties(
        String region,
        String queueUrl) {
}
