package com.sprint.monew.domain.comment.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class CommentNotFoundException extends MonewException {

  public CommentNotFoundException() {
    super(ErrorCode.COMMENT_NOT_FOUND);
  }

  public static CommentNotFoundException withId(UUID id) {
    CommentNotFoundException exception = new CommentNotFoundException();
    exception.addDetail("commentId", id);
    return exception;
  }
}
