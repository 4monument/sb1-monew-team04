package com.sprint.monew.domain.notification;

import com.sprint.monew.common.config.api.NotificationApi;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.notification.dto.NotificationSearchRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor

public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  // 알림 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto> getNotifications(
      @RequestParam(required = false) UUID cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam @Min(1) @Max(100) Integer limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    NotificationSearchRequest request = new NotificationSearchRequest(cursor, after, limit);
    return ResponseEntity.ok(notificationService.getAllNotifications(request, userId));
  }

  // 전체 알림 확인
  @PatchMapping
  public ResponseEntity<Void> checkAllNotifications(
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    notificationService.checkAllNotifications(userId);
    return ResponseEntity.ok().build();
  }

  // 알림 확인(단일)
  @PatchMapping("/{notificationId}")
  public ResponseEntity<Void> checkNotification(@PathVariable UUID notificationId,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    notificationService.checkNotification(notificationId, userId);
    return ResponseEntity.ok().build();
  }
}
