package com.sprint.monew.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArticleRestoreResultDto(
    @Schema(description = "복구 요청 날짜")
    Instant restoreDate,
    @Schema(description = "복구된 기사 ID")
    List<UUID> restoredArticleIds,
    @Schema(description = "복구된 기사 수")
    Long restoredArticleCount
) {

}
