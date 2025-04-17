package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.user.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  List<Notification> findByUser(User user);
}
