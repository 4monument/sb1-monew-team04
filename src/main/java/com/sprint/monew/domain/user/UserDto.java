package com.sprint.monew.domain.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt
) {


}
