package com.sprint.monew.domain.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
    @NotBlank
    @Max(50)
    String nickname
) {

}