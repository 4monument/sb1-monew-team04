package com.sprint.monew.domain.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserControllerTest.TestConfig.class)
class UserControllerTest {

  @Configuration
  @Import(UserController.class)
  static class TestConfig {
    @Bean
    public UserService userService() {
      return org.mockito.Mockito.mock(UserService.class);
    }
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  private UUID userId;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userDto = new UserDto(userId, "test@example.com", "테스트유저", Instant.now());
    org.mockito.Mockito.reset(userService);
  }

  @Test
  @DisplayName("회원 가입 API - 성공")
  void register_Success() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest("test@example.com", "테스트유저", "password123");
    when(userService.register(any(UserRegisterRequest.class))).thenReturn(userDto);

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.nickname").value(userDto.nickname()))
        .andExpect(jsonPath("$.createdAt").exists());

    verify(userService, times(1)).register(any(UserRegisterRequest.class));
  }

  @Test
  @DisplayName("로그인 API - 성공")
  void login_Success() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("test@example.com", "password123");
    when(userService.login(any(UserLoginRequest.class))).thenReturn(userDto);

    // when & then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.nickname").value(userDto.nickname()))
        .andExpect(jsonPath("$.createdAt").exists());

    verify(userService, times(1)).login(any(UserLoginRequest.class));
  }

  @Test
  @DisplayName("닉네임 업데이트 API - 성공")
  void updateNickname_Success() throws Exception {
    // given
    UserUpdateRequest request = new UserUpdateRequest("새닉네임");
    when(userService.updateNickname(any(UUID.class), any(UserUpdateRequest.class))).thenReturn(userDto);

    // when & then
    mockMvc.perform(patch("/api/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(userDto.email()))
        .andExpect(jsonPath("$.nickname").value(userDto.nickname()))
        .andExpect(jsonPath("$.createdAt").exists());

    verify(userService, times(1)).updateNickname(any(UUID.class), any(UserUpdateRequest.class));
  }

  @Test
  @DisplayName("소프트 삭제 API - 성공")
  void softDelete_Success() throws Exception {
    // given
    doNothing().when(userService).softDelete(any(UUID.class));

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).softDelete(userId);
  }

  @Test
  @DisplayName("하드 삭제 API - 성공")
  void hardDelete_Success() throws Exception {
    // given
    doNothing().when(userService).hardDelete(any(UUID.class));

    // when & then
    mockMvc.perform(delete("/api/users/{userId}/hard", userId))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).hardDelete(userId);
  }
}