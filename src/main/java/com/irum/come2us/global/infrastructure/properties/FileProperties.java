package com.irum.come2us.global.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record FileProperties(String storage, String uploadDir, S3 s3) {
    public record S3(String bucket, String baseUrl) {}
}
