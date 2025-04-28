package com.sprint.monew.domain.comment.exception;

import com.sprint.monew.global.error.ErrorCode;
import com.sprint.monew.global.error.MonewException;
import java.util.UUID;

public class LikeAlreadyExistException extends MonewException {

  public LikeAlreadyExistException() {
    super(ErrorCode.LIKE_ALREADY_EXIST);
  }

  public static LikeAlreadyExistException withCommentIdAndUserId(UUID commentId, UUID userId) {
    LikeAlreadyExistException exception = new LikeAlreadyExistException();
    exception.addDetail("commentId", commentId);
    exception.addDetail("userId", userId);
    return exception;
  }
}
