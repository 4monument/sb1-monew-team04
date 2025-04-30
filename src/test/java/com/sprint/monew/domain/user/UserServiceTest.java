package com.sprint.monew.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sprint.monew.domain.activity.UserActivityDto;
import com.sprint.monew.domain.activity.UserActivityService;
import com.sprint.monew.domain.user.exception.EmailAlreadyExistsException;
import com.sprint.monew.domain.user.exception.InvalidCredentialsException;
import com.sprint.monew.domain.user.exception.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserActivityService userActivityService;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private UUID userId;
  private Instant now;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    now = Instant.now();
    testUser = new User(userId, "test@example.com", "테스트유저", "password123", now, false);
  }

  @Test
  @DisplayName("회원 가입 - 성공")
  void register_Success() {
    // given
    UserRegisterRequest request = new UserRegisterRequest("new@example.com", "새유저", "password123");
    UUID savedUserId = UUID.randomUUID();

    // when
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(
        new User(savedUserId, request.email(), request.nickname(), request.password(), now, false)
    );
    when(userActivityService.saveUserActivityToMongo(any(UUID.class)))
        .thenReturn(mock(UserActivityDto.class));

    UserDto result = userService.register(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo(request.email());
    assertThat(result.nickname()).isEqualTo(request.nickname());

    verify(userRepository).existsByEmail(request.email());
    verify(userRepository).save(any(User.class));
    verify(userActivityService).saveUserActivityToMongo(savedUserId);
  }

  @Test
  @DisplayName("회원 가입 - 이미 존재하는 이메일")
  void register_EmailAlreadyExists() {
    // given
    UserRegisterRequest request = new UserRegisterRequest("existing@example.com", "중복유저", "password123");
    when(userRepository.existsByEmail(request.email())).thenReturn(true);

    // when, then
    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class);

    verify(userRepository).existsByEmail(request.email());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("로그인 - 성공")
  void login_Success() {
    // given
    UserLoginRequest request = new UserLoginRequest("test@example.com", "password123");
    when(userRepository.findByEmailAndDeletedFalse(request.email())).thenReturn(Optional.of(testUser));

    // when
    UserDto result = userService.login(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
    assertThat(result.email()).isEqualTo(testUser.getEmail());
    assertThat(result.nickname()).isEqualTo(testUser.getNickname());

    verify(userRepository).findByEmailAndDeletedFalse(request.email());
  }

  @Test
  @DisplayName("로그인 - 사용자 없음")
  void login_UserNotFound() {
    // given
    UserLoginRequest request = new UserLoginRequest("notfound@example.com", "password123");
    when(userRepository.findByEmailAndDeletedFalse(request.email())).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findByEmailAndDeletedFalse(request.email());
  }

  @Test
  @DisplayName("로그인 - 비밀번호 불일치")
  void login_InvalidCredentials() {
    // given
    UserLoginRequest request = new UserLoginRequest("test@example.com", "wrongpassword");
    when(userRepository.findByEmailAndDeletedFalse(request.email())).thenReturn(Optional.of(testUser));

    // when, then
    assertThatThrownBy(() -> userService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);

    verify(userRepository).findByEmailAndDeletedFalse(request.email());
  }

  @Test
  @DisplayName("닉네임 업데이트 - 성공")
  void updateNickname_Success() {
    // given
    UserUpdateRequest request = new UserUpdateRequest("새닉네임");
    when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(testUser));

    // when
    UserDto result = userService.updateNickname(userId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.nickname()).isEqualTo(request.nickname());

    verify(userRepository).findByIdAndDeletedFalse(userId);
  }

  @Test
  @DisplayName("닉네임 업데이트 - 사용자 없음")
  void updateNickname_UserNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새닉네임");
    when(userRepository.findByIdAndDeletedFalse(nonExistingId)).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.updateNickname(nonExistingId, request))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findByIdAndDeletedFalse(nonExistingId);
  }

  @Test
  @DisplayName("소프트 삭제 - 성공")
  void softDelete_Success() {
    // given
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

    // when
    userService.softDelete(userId);

    // then
    assertThat(testUser.isDeleted()).isTrue();
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("소프트 삭제 - 사용자 없음")
  void softDelete_UserNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();
    when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.softDelete(nonExistingId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findById(nonExistingId);
  }

  @Test
  @DisplayName("하드 삭제 - 성공")
  void hardDelete_Success() {
    // given
    when(userRepository.existsById(userId)).thenReturn(true);
    doNothing().when(userRepository).deleteById(userId);

    // when
    userService.hardDelete(userId);

    // then
    verify(userRepository).existsById(userId);
    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("하드 삭제 - 사용자 없음")
  void hardDelete_UserNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();
    when(userRepository.existsById(nonExistingId)).thenReturn(false);

    // when, then
    assertThatThrownBy(() -> userService.hardDelete(nonExistingId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).existsById(nonExistingId);
    verify(userRepository, never()).deleteById(any(UUID.class));
  }
}