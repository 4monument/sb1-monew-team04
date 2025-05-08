package com.sprint.monew.common.scheduler;

import com.sprint.monew.common.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogUploadScheduler {

  private final S3Service s3Service;

  @Value("${log.directory}")
  private String logDirectory;

  /**
   * 매일 새벽 2시에 전날 로그 파일을 S3에 업로드합니다.
   * cron 표현식: 초 분 시 일 월 요일
   */
  @Scheduled(cron = "0 0 2 * * *")
  public void uploadLogFilesToS3() {
    log.info("로그 파일 업로드 스케줄러 시작");

    try {
      File logDir = new File(logDirectory);
      if (!logDir.exists() || !logDir.isDirectory()) {
        log.error("로그 디렉토리를 찾을 수 없습니다: {}", logDirectory);
        return;
      }

      // 어제 날짜 포맷 (로그 파일명에 사용되는 형식에 맞게 조정 필요)
      LocalDate yesterday = LocalDate.now().minusDays(1);
      String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

      // 로그 디렉토리에서 어제 날짜의 로그 파일 찾기
      File[] logFiles = logDir.listFiles((dir, name) -> name.contains(yesterdayStr));

      if (logFiles == null || logFiles.length == 0) {
        log.info("업로드할 로그 파일이 없습니다. 날짜: {}", yesterdayStr);
        return;
      }

      log.info("업로드할 로그 파일 개수: {}", logFiles.length);

      // 각 로그 파일 업로드
      Arrays.stream(logFiles).forEach(file -> {
        String objectKey = "logs/" + yesterday.getYear() + "/" + yesterday.getMonthValue() + "/" + file.getName();
        s3Service.uploadFile(file, objectKey);
      });

      log.info("로그 파일 업로드 완료");
    } catch (Exception e) {
      log.error("로그 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  /**
   * 로그 파일 보관 기간(30일) 이상 지난 파일 삭제
   */
  @Scheduled(cron = "0 30 2 * * *")
  public void deleteOldLogFiles() {
    log.info("오래된 로그 파일 삭제 스케줄러 시작");

    try {
      File logDir = new File(logDirectory);
      if (!logDir.exists() || !logDir.isDirectory()) {
        log.error("로그 디렉토리를 찾을 수 없습니다: {}", logDirectory);
        return;
      }

      // 30일 전 날짜 계산
      LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

      // 로그 디렉토리의 모든 파일 검사
      File[] allLogFiles = logDir.listFiles();
      if (allLogFiles == null || allLogFiles.length == 0) {
        log.info("삭제할 로그 파일이 없습니다.");
        return;
      }

      int deletedCount = 0;
      for (File file : allLogFiles) {
        if (file.isFile() && isOlderThan(file, thirtyDaysAgo)) {
          boolean deleted = file.delete();
          if (deleted) {
            deletedCount++;
            log.info("오래된 로그 파일 삭제: {}", file.getName());
          } else {
            log.warn("로그 파일 삭제 실패: {}", file.getName());
          }
        }
      }

      log.info("삭제된 오래된 로그 파일 개수: {}", deletedCount);
    } catch (Exception e) {
      log.error("오래된 로그 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  /**
   * 파일이 특정 날짜보다 오래되었는지 확인합니다.
   * 파일명에서 날짜를 추출하거나 파일 속성의 수정일을 기준으로 판단할 수 있습니다.
   */
  private boolean isOlderThan(File file, LocalDate date) {
    // 파일명에서 날짜 추출 방식 (로그 파일명 형식에 맞게 조정 필요)
    String fileName = file.getName();
    try {
      // 예: application-2023-12-31.log와 같은 형식이라고 가정
      int dateStartIndex = fileName.indexOf('-') + 1;
      int dateEndIndex = fileName.lastIndexOf('.');
      if (dateStartIndex > 0 && dateEndIndex > dateStartIndex) {
        String dateStr = fileName.substring(dateStartIndex, dateEndIndex);
        LocalDate fileDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return fileDate.isBefore(date);
      }
    } catch (Exception e) {
      // 파일명에서 날짜를 추출할 수 없는 경우 파일 수정 시간으로 확인
      log.debug("파일명에서 날짜 추출 실패, 수정 시간으로 확인: {}", fileName);
    }

    // 파일 수정 시간으로 확인하는 방식
    long lastModified = file.lastModified();
    LocalDate modifiedDate = LocalDate.ofEpochDay(lastModified / (24 * 60 * 60 * 1000));
    return modifiedDate.isBefore(date);
  }
}

