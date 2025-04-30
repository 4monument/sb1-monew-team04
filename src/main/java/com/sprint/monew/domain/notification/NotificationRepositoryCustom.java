package com.sprint.monew.domain.notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {

  List<Notification> getUnconfirmedWithCursor(UUID userId, UUID cursorId,
      Instant afterAt, Pageable pageable);
}
