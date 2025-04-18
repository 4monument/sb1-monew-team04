package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.user.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
}
