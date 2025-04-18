package com.sprint.monew.domain.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt
) {
  public static UserDto from(User user) {
    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreatedAt());
  }
}
