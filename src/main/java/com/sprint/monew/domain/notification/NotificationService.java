package com.sprint.monew.domain.notification;

import com.sprint.monew.common.batch.support.NotificationJdbc;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.notification.dto.NotificationSearchRequest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.notification.exception.NotificationNotFoundException;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  //알림 등록 - 일괄 등록
  public NotificationJdbc createArticleInterestNotifications(UnreadInterestArticleCount unreadInterestArticleCounts) {
    return NotificationJdbc.create(unreadInterestArticleCounts);
  }

  //알림 수정 - 전체 알림 확인
  public void checkAllNotifications(UUID userId) {

    User user = userRepository.findById(userId).orElseThrow();

    List<Notification> notifications = notificationRepository.findByUser(user);
    Instant updatedAt = Instant.now();

    notifications.forEach(n -> {
      n.confirm(updatedAt);
    });

    notificationRepository.saveAll(notifications);
  }

  //알림 수정 - 알림 확인(단일)
  public void checkNotification(UUID notificationId, UUID userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.notFound(notificationId));

    notification.confirm(Instant.now());
    notificationRepository.save(notification);
  }

  //알림 목록 조회
  public CursorPageResponseDto<NotificationDto> getAllNotifications(
      NotificationSearchRequest request, UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    int limit = request.limit();
    UUID cursor = request.cursor();
    Instant after = request.after();

    PageRequest pagerequest = PageRequest.of(0, limit + 1);

    List<Notification> notifications
        = notificationRepository.getUnconfirmedWithCursor(userId, cursor, after,
        pagerequest);

    boolean hasNext = notifications.size() > limit;

    if (hasNext) {
      notifications = notifications.subList(0, limit);
    }

    // 빈 리스트 처리
    if (notifications.isEmpty()) {
      return new CursorPageResponseDto<>(
          Collections.emptyList(),
          null,
          null,
          0,
          0,
          false
      );
    }

    UUID nextCursor = notifications.get(notifications.size() - 1).getId();

    Instant nextAfter = notifications.get(notifications.size() - 1).getCreatedAt();

    int size = Math.min(notifications.size(), limit);

    long totalElements = notificationRepository.countUnconfirmedByUserId(userId);

    List<NotificationDto> notificationDtos = notifications.stream()
        .map(NotificationDto::from)
        .toList();

    return new CursorPageResponseDto<>(
        notificationDtos,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }
}
