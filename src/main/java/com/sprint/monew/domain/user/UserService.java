package com.sprint.monew.domain.user;

import com.sprint.monew.domain.activity.UserActivityMongoRepository;
import com.sprint.monew.domain.activity.UserActivityService;
import com.sprint.monew.domain.user.exception.EmailAlreadyExistsException;
import com.sprint.monew.domain.user.exception.InvalidCredentialsException;
import com.sprint.monew.domain.user.exception.UserAlreadyDeletedException;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
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
  private final UserActivityService userActivityService;
  private final UserActivityMongoRepository userActivityMongoRepository;

  @Transactional
  public UserDto register(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw EmailAlreadyExistsException.withEmail(request.email());
    }
    User user = new User(null, request.email(), request.nickname(),
        request.password(), Instant.now(), false);

    User savedUser = userRepository.save(user);

    userActivityService.synchronizeUserActivityToMongo(savedUser.getId());

    return UserDto.from(savedUser);
  }

  @Transactional(readOnly = true)
  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmailAndDeletedFalse(request.email())
        .orElseThrow(() -> UserNotFoundException.withEmail(request.email()));
    if (!request.password().equals(user.getPassword())) {
      throw InvalidCredentialsException.wrongPassword();
    }
    return UserDto.from(user);
  }

  @Transactional
  public UserDto updateNickname(UUID userId, UserUpdateRequest request) {
    User user = userRepository.findByIdAndDeletedFalse(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    user.updateNickname(request.nickname());
    userActivityService.synchronizeUserActivityToMongo(user.getId());
    return UserDto.from(user);
  }

  @Transactional
  public void softDelete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    if (user.isDeleted()) {
      throw UserAlreadyDeletedException.withUserId(userId);
    }
    user.markDeleted();
  }

  @Transactional
  public void hardDelete(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }
    userActivityMongoRepository.deleteById(userId);
    userRepository.deleteById(userId);
  }
}