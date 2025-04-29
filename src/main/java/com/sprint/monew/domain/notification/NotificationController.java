package com.sprint.monew.domain.notification;

import com.sprint.monew.common.util.CursorPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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


}
