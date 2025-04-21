package com.sprint.monew.common.batch;

import com.sprint.monew.common.batch.config.BatchTestConfig;
import jakarta.annotation.Resource;
import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;


@BatchTestConfig
@SpringBootTest(
    classes = {S3BackupBatch.class},
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
    }
)
@Testcontainers
@ActiveProfiles("test")
class ArticleS3BackupBatchTest {

  private static S3Client s3Client;
  private static String accessKey;
  private static String secretKey;
  private static Region region;
  private static String bucketName;
  private static URI endpoint;
  private static final String contentType = "image/jpeg";
  private static final String KEY = UUID.randomUUID().toString();

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;

  @Resource(name = "s3BackupJob")
  Job s3BackupJob;

  @Container
  static LocalStackContainer localStackContainer = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:latest")
  ).withServices(LocalStackContainer.Service.S3);

  @BeforeAll
  static void setUp() {
    accessKey = localStackContainer.getAccessKey();
    secretKey = localStackContainer.getSecretKey();
    region = Region.of(localStackContainer.getRegion());
    bucketName = "s3-backup-bucket";
    endpoint = localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3);
    s3Client = S3Client.builder()
        .endpointOverride(endpoint)
        .region(region)
        .credentialsProvider(generateCredentialsProvider())
        .forcePathStyle(true)
        .build();
    s3Client.createBucket(b -> b.bucket(bucketName));
  }

  @Test
  void test() throws Exception {
//    jobLauncherTestUtils.setJob(s3BackupJob);
//    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    // given
    System.out.println("s3BackupJob.getName() = " + s3BackupJob.getName());
    jobLauncherTestUtils.setJob(s3BackupJob);
    jobLauncherTestUtils.launchJob();
    // when

    // then
  }

  private static StaticCredentialsProvider generateCredentialsProvider() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    return StaticCredentialsProvider.create(credentials);
  }
}