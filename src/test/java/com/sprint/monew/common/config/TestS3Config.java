package com.sprint.monew.common.config;

import java.net.URI;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
@Profile("test")
public class TestS3Config {

  @Bean
  @Primary
  public S3Client testS3Client() {
    // 테스트용 더미 자격 증명
    AwsBasicCredentials credentials = AwsBasicCredentials.create("test", "test");

    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.AP_NORTHEAST_2)
        .endpointOverride(URI.create("http://localhost:4566")) // 로컬 엔드포인트
        .forcePathStyle(true) // S3 Path 스타일 사용
        .build();

  }
}