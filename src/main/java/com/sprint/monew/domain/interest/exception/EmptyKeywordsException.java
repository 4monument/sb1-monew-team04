package com.sprint.monew.domain.interest.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;

public class EmptyKeywordsException extends MonewException {

  public EmptyKeywordsException() {
    super(ErrorCode.EMPTY_KEYWORDS_NOT_ALLOWED);
  }

  public static EmptyKeywordsException emptyKeywords() {
    return new EmptyKeywordsException();
  }
}
