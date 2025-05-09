package com.sprint.monew.common.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.monew.common.util.S3Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LogUploadSchedulerTest {

  @Mock
  private S3Service s3Service;

  @InjectMocks
  private LogUploadScheduler scheduler;

  private Path tempLogDir;

  @BeforeEach
  void setUp() throws IOException {
    // 테스트용 임시 디렉토리 생성
    tempLogDir = Files.createTempDirectory("test-logs");

    // 스케줄러의 logDirectory 필드를 테스트용 임시 디렉토리로 설정
    ReflectionTestUtils.setField(scheduler, "logDirectory", tempLogDir.toString());
  }

  @AfterEach
  void tearDown() throws IOException {
    // 테스트 후 임시 디렉토리 및 파일 정리
    Files.walk(tempLogDir)
        .sorted((a, b) -> -a.compareTo(b)) // 역순으로 정렬하여 파일 먼저 삭제 후 디렉토리 삭제
        .forEach(path -> {
          try {
            Files.delete(path);
          } catch (IOException e) {
            System.err.println("파일 삭제 실패: " + path);
          }
        });
  }

  @Test
  @DisplayName("어제 날짜의 로그 파일 S3 업로드 테스트")
  void uploadYesterdayLogFilesToS3() throws IOException {
    // given
    LocalDate yesterday = LocalDate.now().minusDays(1);
    String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    // 어제 날짜의 테스트 로그 파일 2개 생성
    Path logFile1 = Files.createFile(tempLogDir.resolve("application-" + yesterdayStr + ".log"));
    Path logFile2 = Files.createFile(tempLogDir.resolve("error-" + yesterdayStr + ".log"));

    // 다른 날짜의 로그 파일도 생성 (업로드 대상 아님)
    Path oldLogFile = Files.createFile(
        tempLogDir.resolve("application-" + LocalDate.now().minusDays(2) + ".log"));

    // when
    scheduler.uploadLogFilesToS3();

    // then
    // 어제 날짜의 로그 파일 2개만 업로드되었는지 확인
    verify(s3Service, times(2)).uploadFile(any(File.class), anyString());
  }

  @Test
  @DisplayName("어제 날짜의 로그 파일이 없을 때 S3 업로드 시도하지 않음")
  void doNotUploadWhenNoYesterdayLogFiles() throws IOException {
    // given
    // 어제 날짜가 아닌 다른 날짜의 로그 파일만 생성
    LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
    String twoDaysAgoStr = twoDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Path oldLogFile = Files.createFile(tempLogDir.resolve("application-" + twoDaysAgoStr + ".log"));

    // when
    scheduler.uploadLogFilesToS3();

    // then
    // S3 업로드가 호출되지 않았는지 확인
    verify(s3Service, never()).uploadFile(any(File.class), anyString());
  }

  @Test
  @DisplayName("30일 이상 지난 로그 파일 삭제 테스트")
  void deleteOldLogFiles() throws IOException {
    // given
    // 현재 날짜의 로그 파일 생성 (삭제 대상 아님)
    String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Path currentLogFile = Files.createFile(tempLogDir.resolve("application-" + todayStr + ".log"));

    // 31일 전 날짜의 로그 파일 생성 (삭제 대상)
    String oldDateStr = LocalDate.now().minusDays(31)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    Path oldLogFile = Files.createFile(tempLogDir.resolve("application-" + oldDateStr + ".log"));

    // 파일 수정 시간을 31일 전으로 설정
    oldLogFile.toFile().setLastModified(
        System.currentTimeMillis() - 31L * 24 * 60 * 60 * 1000);

    // when
    scheduler.deleteOldLogFiles();

    // then
    // 현재 로그 파일은 여전히 존재하는지 확인하기
    assert Files.exists(currentLogFile);

    // 오래된 로그 파일은 삭제되었는지 확인
    assertFalse(Files.exists(oldLogFile), "오래된 로그 파일은 삭제되어야 함");

    // 파일 개수로 확인
    int finalFileCount = tempLogDir.toFile().list().length;
    assertEquals(1, finalFileCount, "삭제 후 1개의 파일만 남아있어야 함");
  }

  @Test
  @DisplayName("로그 디렉토리가 존재하지 않을 때 예외 발생하지 않음")
  void handleNonExistentLogDirectory() {
    // given
    // 존재하지 않는 디렉토리로 설정
    ReflectionTestUtils.setField(scheduler, "logDirectory", "/non/existent/directory");

    // when & then
    // 예외가 발생하지 않고 정상적으로 실행되는지 확인
    scheduler.uploadLogFilesToS3();
    scheduler.deleteOldLogFiles();

    // S3 업로드가 호출되지 않았는지 확인
    verify(s3Service, never()).uploadFile(any(File.class), anyString());
  }
}