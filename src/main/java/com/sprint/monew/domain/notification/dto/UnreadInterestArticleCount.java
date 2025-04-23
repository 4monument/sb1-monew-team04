package com.sprint.monew.domain.notification.dto;

import java.util.UUID;

public interface UnreadInterestArticleCount {

  UUID getInterestId();

  String getInterestName();

  Long getUnreadCount();
}
