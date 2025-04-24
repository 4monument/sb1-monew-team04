package com.sprint.monew.domain.notification.dto;

import com.sprint.monew.domain.interest.Interest;
import com.sprint.monew.domain.user.User;

public interface UnreadInterestArticleCount {

  Interest getInterest();

  User getUser();

  Long getTotalNewArticles();
}
