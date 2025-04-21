package com.sprint.monew.global.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "aws.s3")
@Validated
public record S3ConfigProperties(

    @NotBlank(message = "AWS S3 access-key를 지정해주세요.")
    String accessKey,
    @NotBlank(message = "AWS S3 secret-key를 지정해주세요.")
    String secretKey,
    @NotBlank(message = "AWS S3 bucketname이 지정해주세요.")
    String bucketName,
    @NotBlank(message = "AWS S3 region을 지정해주세요")
    String region) {
}
