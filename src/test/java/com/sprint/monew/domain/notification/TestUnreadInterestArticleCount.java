package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import java.util.UUID;

public class TestUnreadInterestArticleCount implements UnreadInterestArticleCount {

  private UUID interestId;
  private String interestName;
  private Long unreadCount;

  public TestUnreadInterestArticleCount(UUID interestId, String interestName, Long unreadCount) {
    this.interestId = interestId;
    this.interestName = interestName;
    this.unreadCount = unreadCount;
  }

  @Override
  public UUID getInterestId() {
    return interestId;
  }

  @Override
  public String getInterestName() {
    return interestName;
  }

  @Override
  public Long getUnreadCount() {
    return unreadCount;
  }
}
