package com.sprint.monew.domain.comment.dto.request;

import java.util.UUID;

public record CommentRegisterRequest (
    UUID articleId,
    UUID userId,
    String content
) {

}
