package com.sprint.monew.domain.user;

import com.sprint.monew.common.config.api.UserApi;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> register(@RequestBody UserRegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request) {
    return ResponseEntity.ok(userService.login(request));
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateNickname(@PathVariable UUID userId,
      @RequestBody UserUpdateRequest request) {
    return ResponseEntity.ok(userService.updateNickname(userId, request));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> softDelete(@PathVariable UUID userId) {
    userService.softDelete(userId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{userId}/hard")
  public ResponseEntity<Void> hardDelete(@PathVariable UUID userId) {
    userService.hardDelete(userId);
    return ResponseEntity.noContent().build();
  }
}
