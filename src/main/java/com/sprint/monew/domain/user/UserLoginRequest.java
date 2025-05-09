package com.sprint.monew.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @Email
    String email,
    @NotBlank
    String password
) {

}