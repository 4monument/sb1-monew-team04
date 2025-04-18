package com.sprint.monew.domain.notification;

import java.time.Instant;
import java.util.List;

public class CursorPageResponseNotificationDto {

  private List<NotificationDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private int totalElements;
  private boolean hasNext;
}
