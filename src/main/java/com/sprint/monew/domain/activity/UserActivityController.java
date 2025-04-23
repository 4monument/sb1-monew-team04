package com.sprint.monew.domain.activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user-activity")
@RequiredArgsConstructor
public class UserActivityController {
    private final UserActivityService userActivityService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(
            @PathVariable UUID userId,
            @RequestHeader("Monew-Request-User-ID") UUID headerUserId
    ) {
        return ResponseEntity.ok(userActivityService.getUserActivity(userId));
    }
}
