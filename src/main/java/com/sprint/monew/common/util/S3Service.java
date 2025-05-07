package com.sprint.monew.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName; // 혹은 @Value 사용

  public void uploadFile(File file, String objectKey) {
    try {
      log.info("S3 업로드 시작: {}", file.getName());

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(objectKey)
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

      log.info("S3 업로드 완료: {}", objectKey);
    } catch (Exception e) {
      log.error("S3 업로드 중 오류 발생: {}", e.getMessage(), e);
      throw new RuntimeException("S3 업로드 실패", e);
    }
  }
}
