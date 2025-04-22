package com.sprint.monew.global.config;

import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3OutputStreamProvider;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3ConfigProperties.class)
@RequiredArgsConstructor
public class S3Config {

  private final S3ConfigProperties s3Properties;

  @Primary
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

//  // 멀티파트, 메모리 완화 기능 위해 S3Resource.create 사용 및 S3Resource로 반환
//  @Bean(name = "articleS3Resource")
//  public S3Resource articleS3Resource(S3OutputStreamProvider s3OutputStreamProvider) {
//    //s3Operations.createResource(s3Properties.bucketName(), ...)
//    String location = "s3://" + s3Properties.bucket() + "/";
//    return S3Resource.create(location, s3Client(), s3OutputStreamProvider);
//  }

//  @Bean
//  public Resource resourceLoader(ResourceLoader resourceLoader) {
//    String location = "s3://" + s3Properties.bucketName();
//    return resourceLoader.getResource(location);
//  }
}
