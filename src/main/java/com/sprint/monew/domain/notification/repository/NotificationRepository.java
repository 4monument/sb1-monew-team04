package com.sprint.monew.domain.notification.repository;

import com.sprint.monew.domain.notification.Notification;
import com.sprint.monew.domain.user.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID>,
    NotificationRepositoryCustom {

  List<Notification> findByUser(User user);

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Notification n " +
      "where n.confirmed = true " +
      "and n.updatedAt < :confirmedAt")
  void deleteConfirmedNotificationsOlderThan(@Param("confirmedAt") Instant confirmedAt);

  @Query(
      "SELECT n "
          + "FROM Notification n "
          + "WHERE n.user.id = :userId AND n.confirmed = false "
          // 커서가 없거나, 생성 시간이 이것보다 작거나, 생성 시간이 같다면 ID가 이것보다 작은
          + "AND (:cursorId IS NULL OR n.createdAt < :afterAt OR (n.createdAt = :afterAt AND n.id < :cursorId))"
          + "ORDER BY n.createdAt DESC, n.id DESC")
  List<Notification> findUnconfirmedWithCursor(UUID userId, UUID cursorId,
      Instant afterAt, Pageable pageable);

  @Query(
      "SELECT COUNT(n) "
          + "FROM Notification n "
          + "WHERE n.user.id = :userId AND n.confirmed = false")
  int countUnconfirmedByUserId(UUID userId);

}
