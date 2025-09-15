package co.com.pragma.dynamodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.dynamo")
public record DynamoDBProperties (
        String region) {
    }