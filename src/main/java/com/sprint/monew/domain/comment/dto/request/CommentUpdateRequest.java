package com.sprint.monew.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest (
    @NotBlank
    @Size(min = 1, max = 255)
    String content
) {

}
