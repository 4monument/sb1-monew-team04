package com.sprint.monew.domain.article.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class ArticleNotFoundException extends MonewException {

  public ArticleNotFoundException() {
    super(ErrorCode.ARTICLE_NOT_FOUND);
  }

  public static ArticleNotFoundException withId(UUID id) {
    ArticleNotFoundException exception = new ArticleNotFoundException();
    exception.addDetail("articleId", id);
    return exception;
  }
}
