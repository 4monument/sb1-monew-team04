package com.sprint.monew.domain.notification;

import com.sprint.monew.common.batch.support.NotificationJdbc;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.notification.dto.NotificationDto;
import com.sprint.monew.domain.notification.dto.NotificationSearchRequest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.notification.exception.NotificationNotFoundException;
import com.sprint.monew.domain.notification.repository.NotificationRepository;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  //알림 등록
  public NotificationJdbc createArticleInterestNotifications(
      UnreadInterestArticleCount unreadInterestArticleCounts) {
    return NotificationJdbc.create(unreadInterestArticleCounts);
  }

  //알림 수정 - 전체 알림 확인
  @Transactional
  public void checkAllNotifications(UUID userId) {

    log.info("알림 수정-전체 알림 확인. 요청자 ID = {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    List<Notification> notifications = notificationRepository.findByUser(user);
    Instant updatedAt = Instant.now();

    log.debug("미확인 알림 수 = {}", notifications.size());

    notifications.forEach(n -> {
      n.confirm(updatedAt);
    });

    notificationRepository.saveAll(notifications);

    log.info("알림 수정-전체 알림 확인 완료. 확인된 알림 수 = {}, 확인 시각 = {}", notifications.size(), Instant.now());
  }

  //알림 수정 - 알림 확인(단일)
  @Transactional
  public void checkNotification(UUID notificationId, UUID userId) {

    log.info("알림 수정-단일 알림 확인. 요청자 ID = {}, 알림 ID = {}", userId, notificationId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.notFound(notificationId));

    log.debug("확인 여부 = {}", notification.isConfirmed());

    notification.confirm(Instant.now());
    notificationRepository.save(notification);

    log.info("알림 수정-단일 알림 확인 완료. 확인 여부 = {}, 확인 시각 = {}", notification.isConfirmed(),
        Instant.now());
  }

  //알림 목록 조회
  @Transactional(readOnly = true)
  public CursorPageResponseDto<NotificationDto> getAllNotifications(
      NotificationSearchRequest request, UUID userId) {

    log.info("알림 조회 시작. 요청자 ID = {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    int limit = (request.limit() == null || request.limit() <= 0) ? 30 : request.limit();
    UUID cursor = request.cursor();
    Instant after = request.after();

    PageRequest pagerequest = PageRequest.of(0, limit + 1);

    List<Notification> notifications
        = notificationRepository.getUnconfirmedWithCursor(userId, cursor, after,
        pagerequest);

    log.debug("검색 및 정렬 조건 만족하는 검색 결과 수 = {}", notifications.size());

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

    log.info("알림 조회 완료");

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
