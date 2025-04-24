package com.sprint.monew.common.util;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseDto<T>(
    List<T> content,
    Object nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
