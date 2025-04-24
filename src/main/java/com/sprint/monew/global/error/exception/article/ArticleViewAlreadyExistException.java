package com.sprint.monew.global.error.exception.article;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class ArticleViewAlreadyExistException extends MonewException {

  public ArticleViewAlreadyExistException() {
    super(ErrorCode.ARTICLE_VIEW_ALREADY_EXIST);
  }

  public static ArticleViewAlreadyExistException withId(UUID userId, UUID articleId) {
    ArticleViewAlreadyExistException exception = new ArticleViewAlreadyExistException();
    exception.addDetail("userId", userId);
    exception.addDetail("articleId", articleId);
    return exception;
  }

}
