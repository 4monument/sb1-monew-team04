package com.sprint.monew.domain.user;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public UserDto register(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email already in use");
    }
    User user = new User(null, request.email(), request.nickname(),
        request.password(), Instant.now(), false);
    return UserDto.from(userRepository.save(user));
  }

  @Transactional(readOnly = true)
  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmailAndDeletedFalse(request.email())
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    if (!request.password().equals(user.getPassword())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    return UserDto.from(user);
  }

  @Transactional
  public UserDto updateNickname(UUID userId, UserUpdateRequest request) {
    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    user.updateNickname(request.nickname());
    return UserDto.from(user);
  }

  @Transactional
  public void softDelete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    user.markDeleted();
  }

  @Transactional
  public void hardDelete(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found");
    }
    userRepository.deleteById(userId);
  }
}