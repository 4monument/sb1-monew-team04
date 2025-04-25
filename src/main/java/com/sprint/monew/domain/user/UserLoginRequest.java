package com.sprint.monew.domain.user;

public record UserLoginRequest(
    String email,
    String password
) {

}