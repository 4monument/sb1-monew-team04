package com.sprint.monew.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3ConfigProperties.class)
@RequiredArgsConstructor
public class S3Config {

  private final S3ConfigProperties s3Properties;

  @Bean
  public S3Client s3Client() {
    AwsBasicCredentials credentials = getAwsBasicCredentials();
    Region s3Region = Region.of(s3Properties.region());

    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(s3Region)
        .build();
  }

  private AwsBasicCredentials getAwsBasicCredentials() {
    return AwsBasicCredentials.create(s3Properties.accessKey(),
        s3Properties.secretKey());
  }
}
