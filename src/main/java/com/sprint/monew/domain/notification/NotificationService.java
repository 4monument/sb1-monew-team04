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
  public void checkAllNotifications(String userId) {
    User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();

    List<Notification> notifications = notificationRepository.findByUser(user);
    Instant updatedAt = Instant.now();

    notifications.forEach(n -> {
      n.confirm(updatedAt);
    });

    notificationRepository.saveAll(notifications);

  }

  //알림 수정 - 알림 확인(단일)
  public void checkNotification(String notificationId, String userId) {
    User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();
    Notification notification = notificationRepository.findById(UUID.fromString(notificationId))
        .orElseThrow();
    notification.confirm(Instant.now());
    notificationRepository.save(notification);
  }

  //알림 삭제
  public void deleteNotification(String notificationId, String userId) {

  }


  //알림 목록 조회
  public CursorPageResponseNotificationDto getAllNotifications(String cursor, String after,
      int limit, String userId) {
    User user = userRepository.findById(UUID.fromString(userId)).orElseThrow();

    List<Notification> notifications = notificationRepository.findByUser(user);
    //todo-페이지네이션 구현 및 적용
    return null;
  }

}
