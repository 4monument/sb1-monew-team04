package com.sprint.monew.domain.comment.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class CommentNotOwnedException extends MonewException {

  public CommentNotOwnedException() {
    super(ErrorCode.COMMENT_NOT_OWNED);
  }

  public static CommentNotOwnedException withCommentIdAndUserId(UUID commentId, UUID userId) {
    CommentNotOwnedException exception = new CommentNotOwnedException();
    exception.addDetail("commentId", commentId);
    exception.addDetail("userId", userId);
    return exception;
  }
}
