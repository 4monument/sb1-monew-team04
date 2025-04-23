package com.sprint.monew.domain.notification;

import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.interest.subscription.SubscriptionRepository;
import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
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
  private final SubscriptionRepository subscriberRepository;

  //알림 등록 - 일괄 등록
  public List<Notification> createArticleInterestNotifications(Instant afterAt) {

    List<UnreadInterestArticleCount> unreadInterestArticleCounts
        = subscriberRepository.findNewArticleCountWithUserInterest(afterAt);

    List<Notification> newNotifications = new ArrayList<>();

    for (UnreadInterestArticleCount unreadInterestArticleCount : unreadInterestArticleCounts) {
      User user = unreadInterestArticleCount.getUser();
      Interest interest = unreadInterestArticleCount.getInterest();
      long totalNewArticles = unreadInterestArticleCount.getTotalNewArticles();
      Notification notification = new Notification(
          user,
          interest.getId(),
          ResourceType.INTEREST,
          interest.getName() + "와/과 관련된 기사가 " + totalNewArticles + "건 등록되었습니다.");

      newNotifications.add(notification);

      //아래 save는 배치로 한번에 하는건지, 아니면 메소드 내부에서 해야하는건지?
      //전자라면 주석 해제, 후자라면 save 코드 지우고 만들어진 알림 리스트로 보내주기만 하면된다.
      //notificationRepository.save(notification);
    }
    return newNotifications;
  }

  //알림 등록 - 좋아요
  public List<Notification> createLikeNotification(User userId) {
    List<Notification> notifications = new ArrayList<>();
    return notifications;
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
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

    notification.confirm(Instant.now());
    notificationRepository.save(notification);
  }

  //알림 목록 조회
  public CursorPageResponseDto<NotificationDto> getAllNotifications(UUID cursor, Instant after,
      int limit, UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

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

    long totalElements = notificationRepository.countUnconfirmedByUserId(userId);

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
