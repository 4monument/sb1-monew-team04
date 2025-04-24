package com.sprint.monew.domain.user;

public record UserRegisterRequest(
    String email,
    String nickname,
    String password
) {

}

