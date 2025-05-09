package com.sprint.monew.domain.notification.repository;

import com.sprint.monew.domain.notification.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {

  List<Notification> getUnconfirmedWithCursor(UUID userId, Instant cursorId,
      Instant afterAt, Pageable pageable);
}
