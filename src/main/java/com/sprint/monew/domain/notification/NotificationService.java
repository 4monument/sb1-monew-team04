package com.sprint.monew.domain.notification;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.userinterest.UserInterestRepository;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final UserInterestRepository subscriberRepository;

  //알림 등록 - 일괄 등록
  public List<NotificationDto> createNotifications(UUID userId) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    List<UnreadInterestArticleCount> unreadInterestArticleCounts = subscriberRepository.countUnreadArticlesByInterest(
        user.getId());

    List<NotificationDto> notificationDtos = new ArrayList<>();

    for (UnreadInterestArticleCount unreadInterestArticleCount : unreadInterestArticleCounts) {
      Notification notification = new Notification(user, unreadInterestArticleCount.getInterestId(),
          ResourceType.INTEREST, unreadInterestArticleCount.getInterestName() + "와/과 관련된 기사가 "
          + unreadInterestArticleCount.getUnreadCount() + "건 등록되었습니다.");

      notificationRepository.save(notification);
      notificationDtos.add(NotificationDto.from(notification));
    }

    return notificationDtos;
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

    User user = userRepository.findById(userId).orElseThrow(
        () -> new IllegalArgumentException("User not found")
    );

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

    notification.confirm(Instant.now());
    notificationRepository.save(notification);
  }

  //알림 목록 조회
  public CursorPageResponseDto<NotificationDto> getAllNotifications(UUID cursor, Instant after,
      int limit, UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new IllegalArgumentException("User not found")
    );

    createNotifications(user.getId());

    PageRequest pagerequest = PageRequest.of(0, limit + 1);

    List<Notification> notifications
        = notificationRepository.findUnconfirmedWithCursor(userId, cursor, after,
        pagerequest);

    boolean hasNext = notifications.size() > limit;

    if (hasNext) {
      notifications = notifications.subList(0, limit);
    }

    UUID nextCursor = notifications.get(notifications.size() - 1).getId();

    Instant nextAfter = notifications.get(notifications.size() - 1).getCreatedAt();

    int size = Math.min(notifications.size(), limit);

    long totalElements = notifications.size();

    List<NotificationDto> notificationDtos = notifications.stream()
        .map(NotificationDto::from)
        .toList();

    return new CursorPageResponseDto(
        notificationDtos,
        nextCursor,
        nextAfter,
        size,
        totalElements,
        hasNext
    );
  }
}
