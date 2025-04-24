package com.sprint.monew.domain.notification;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.notification.dto.UnreadInterestArticleCount;
import com.sprint.monew.domain.user.User;

public class TestUnreadInterestArticleCount implements UnreadInterestArticleCount {

  private Interest interest;
  private User user;
  private Long totalNewArticles;

  public TestUnreadInterestArticleCount(Interest interest, User user, Long totalNewArticles) {
    this.interest = interest;
    this.user = user;
    this.totalNewArticles = totalNewArticles;
  }
  
  @Override
  public Interest getInterest() {
    return interest;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public Long getTotalNewArticles() {
    return totalNewArticles;
  }
}
