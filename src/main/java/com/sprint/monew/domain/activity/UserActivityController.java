package com.sprint.monew.domain.activity;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

  private final UserActivityService userActivityService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserActivityDto> getUserActivityFromMongo(
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  ) {
    return ResponseEntity.ok(userActivityService.getUserActivityFromMongo(userId));
  }

  @GetMapping("/{userId}/query")
  public ResponseEntity<UserActivityDto> getUserActivity(
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  ) {
    return ResponseEntity.ok(userActivityService.getUserActivity(userId));
  }

  @GetMapping("/{userId}/save")
  public ResponseEntity<UserActivityDto> saveUserActivityToMongo(
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  ) {
    return ResponseEntity.ok(userActivityService.saveUserActivityToMongo(userId));
  }

  @GetMapping("/{userId}/update")
  public ResponseEntity<Void> updateUserActivityToMongo(
      @PathVariable UUID userId,
      @RequestHeader(name = "Monew-Request-User-ID", required = false) UUID headerUserId
  ) {
    userActivityService.updateUserActivity(userId);
    return ResponseEntity.ok(null);
  }
}
