package com.sprint.monew.domain.notification.exception;

import com.sprint.monew.global.error.ErrorCode;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException() {
    super(ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationNotFoundException notFound(UUID notificationId) {
    NotificationNotFoundException exception = new NotificationNotFoundException();
    exception.addDetail("notificationId", notificationId);
    return exception;
  }

}
