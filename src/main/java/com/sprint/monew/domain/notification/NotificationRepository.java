package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.user.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  List<Notification> findByUser(User user);

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Notification n " +
      "where n.confirmed = true " +
      "and n.updatedAt > :confirmedAt")
  void deleteConfirmedNotificationsOlderThan(@Param("confirmedAt") Instant confirmedAt);

  @Query(
      "SELECT n "
          + "FROM Notification n "
          + "WHERE n.user.id = :userId "
          + "AND (( CAST( :cursorId as UUID ) IS NULL OR n.id < :cursorId )"
          + "OR ( cast( :afterAt as timestamp ) IS NULL OR n.createdAt < :afterAt ))"
          + "ORDER BY n.createdAt DESC, n.id DESC"
  )
  List<Notification> findByConfirmedFalseAndUserIdWithCursor(UUID userId, UUID cursorId,
      Instant afterAt, Pageable pageable);
}
