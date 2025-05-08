package com.sprint.monew.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
    @Email
    String email,
    @NotBlank
    @Size(min = 1, max = 50)
    String nickname,
    @NotBlank
    String password
) {

}

