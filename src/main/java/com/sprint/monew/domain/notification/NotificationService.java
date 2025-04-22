package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  //알림 등록
  public void createNotification(String notificationId, String userId) {

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
