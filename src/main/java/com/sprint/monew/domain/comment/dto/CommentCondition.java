package com.sprint.monew.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentCondition(
    UUID articleId,
    String cursor,
    Instant after
) {

}
