package com.sprint.monew.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CommentRegisterRequest (
    @NotNull
    UUID articleId,
    @NotNull
    UUID userId,
    @NotBlank
    //api 문서에는 최대값이 500으로 되어 있지만 저희 ERD에는 255로 되어 있어서 255로 설정했습니다.
    @Size(min = 1, max = 255)
    String content
) {

}
