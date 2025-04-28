package com.sprint.monew.domain.notification;

import com.sprint.monew.common.util.CursorPageResponseDto;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor

public class NotificationController {

  private final NotificationService notificationService;

  // 알림 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto> getNotifications() {
    return null;
  }

  // 전체 알림 확인
  @PatchMapping
  public ResponseEntity<?> checkAllNotifications() {
    //Monew-Request-User-ID는 헤더로 받음
    return null;
  }

  // 알림 확인(단일)
  @PatchMapping("/{notificationId}")
  public ResponseEntity<?> checkNotification(@PathVariable String notificationId) {
    //Monew-Request-User-ID는 헤더로 받음
    return null;
  }

  //테스트용 생성
  @PostMapping
  public ResponseEntity<List<Notification>> createNotification(
      @RequestParam Instant afterAt) {
    return ResponseEntity.ok(notificationService.createArticleInterestNotifications(afterAt));
  }

}
