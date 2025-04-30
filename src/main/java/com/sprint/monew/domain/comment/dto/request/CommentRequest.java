package com.sprint.monew.domain.comment.dto.request;

import java.time.Instant;
import java.util.UUID;

public record CommentRequest(
    UUID articleId,
    String cursor,
    Instant after
) {
}
