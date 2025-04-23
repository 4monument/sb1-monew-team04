package com.sprint.monew.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.monew.PostgresContainer;
import com.sprint.monew.domain.user.UserLoginRequest;
import com.sprint.monew.domain.user.UserRegisterRequest;
import com.sprint.monew.domain.user.UserUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  static final PostgresContainer postgresContainer = PostgresContainer.getInstance();

  static {
    postgresContainer.start();
  }

  @DynamicPropertySource
  static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @Test
  @DisplayName("사용자 생성 API 통합테스트")
  void UserRegister_Success() throws Exception {
    //Given
    // 1. 사용자 등록
    UserRegisterRequest createRequest = new UserRegisterRequest(
        "test@test.com",
        "test",
        "test123"
    );

    String requestBody = objectMapper.writeValueAsString(createRequest);

    // When & Then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.email", is("test@test.com")))
        .andExpect(jsonPath("$.nickname", is("test")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  @DisplayName("사용자 생성 API 실패 - 중복된 이메일")
  void userRegister_Fail_DuplicateEmail() throws Exception {
    // Given
    UserRegisterRequest firstRequest = new UserRegisterRequest(
        "duplicate@test.com",
        "test1",
        "test123");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstRequest)))
        .andExpect(status().isCreated());

    UserRegisterRequest duplicateRequest = new UserRegisterRequest(
        "duplicate@test.com",
        "test2",
        "test456");

    String requestBody = objectMapper.writeValueAsString(duplicateRequest);

    // When & Then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("사용자 로그인 API 통합 테스트")
  void userLogin_Success() throws Exception {
    // Given
    // 1. 사용자 등록
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "login@test.com",
        "loginTest",
        "password123"
    );

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    // 2. 로그인 요청 준비
    UserLoginRequest loginRequest = new UserLoginRequest(
        "login@test.com",
        "password123"
    );

    String loginRequestBody = objectMapper.writeValueAsString(loginRequest);

    // When & Then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginRequestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.email", is("login@test.com")))
        .andExpect(jsonPath("$.nickname", is("loginTest")));
  }

  @Test
  @DisplayName("사용자 로그인 실패 - 존재하지 않는 이메일")
  void userLogin_Fail_NonExistentEmail() throws Exception {
    // Given
    UserLoginRequest loginRequest = new UserLoginRequest(
        "nonexistent@test.com",
        "password123"
    );

    String requestBody = objectMapper.writeValueAsString(loginRequest);

    // When & Then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 로그인 실패 - 잘못된 비밀번호")
  void userLogin_Fail_WrongPassword() throws Exception {
    // Given
    // 1. 사용자 등록
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "password@test.com",
        "pwTest",
        "correctPassword"
    );

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated());

    // 2. 잘못된 비밀번호로 로그인 시도
    UserLoginRequest wrongPasswordRequest = new UserLoginRequest(
        "password@test.com",
        "wrongPassword"
    );

    String loginRequestBody = objectMapper.writeValueAsString(wrongPasswordRequest);

    // When & Then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginRequestBody))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("사용자 닉네임 업데이트 API 통합 테스트")
  void updateNickname_Success() throws Exception {
    // Given
    // 1. 사용자 등록
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "update@test.com",
        "oldNickname",
        "password123"
    );

    MvcResult registerResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    // 2. 생성된 사용자 ID 추출
    String responseJson = registerResult.getResponse().getContentAsString();
    String userId = objectMapper.readTree(responseJson).get("id").asText();

    // 3. 닉네임 업데이트 요청 준비
    UserUpdateRequest updateRequest = new UserUpdateRequest("newNickname");
    String updateRequestBody = objectMapper.writeValueAsString(updateRequest);

    // When & Then
    mockMvc.perform(patch("/api/users/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateRequestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(userId)))
        .andExpect(jsonPath("$.email", is("update@test.com")))
        .andExpect(jsonPath("$.nickname", is("newNickname")));
  }

  @Test
  @DisplayName("닉네임 업데이트 실패 - 존재하지 않는 사용자")
  void updateNickname_Fail_NonExistentUser() throws Exception {
    // Given
    String nonExistentUserId = UUID.randomUUID().toString();

    UserUpdateRequest updateRequest = new UserUpdateRequest("newNickname");
    String updateRequestBody = objectMapper.writeValueAsString(updateRequest);

    // When & Then
    mockMvc.perform(patch("/api/users/" + nonExistentUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateRequestBody))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 소프트 삭제 API 통합 테스트")
  void softDelete_Success() throws Exception {
    // Given
    // 1. 사용자 등록
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "delete@test.com",
        "deleteTest",
        "password123"
    );

    MvcResult registerResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    // 2. 생성된 사용자 ID 추출
    String responseJson = registerResult.getResponse().getContentAsString();
    String userId = objectMapper.readTree(responseJson).get("id").asText();

    // When & Then
    mockMvc.perform(delete("/api/users/" + userId))
        .andExpect(status().isNoContent());

    // 3. 로그인 시도 - 삭제된 계정으로 로그인 불가 확인
    UserLoginRequest loginRequest = new UserLoginRequest(
        "delete@test.com",
        "password123"
    );

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("소프트 삭제 실패 - 존재하지 않는 사용자")
  void softDelete_Fail_NonExistentUser() throws Exception {
    // Given
    String nonExistentUserId = UUID.randomUUID().toString();

    // When & Then
    mockMvc.perform(delete("/api/users/" + nonExistentUserId))
        .andExpect(status().isNotFound()); // 404 Not Found 예상
  }

  @Test
  @DisplayName("사용자 하드 삭제 API 통합 테스트")
  void hardDelete_Success() throws Exception {
    // Given
    // 1. 사용자 등록
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        "harddelete@test.com",
        "hardDeleteTest",
        "password123"
    );

    MvcResult registerResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    // 2. 생성된 사용자 ID 추출
    String responseJson = registerResult.getResponse().getContentAsString();
    String userId = objectMapper.readTree(responseJson).get("id").asText();

    // When & Then
    mockMvc.perform(delete("/api/users/" + userId + "/hard"))
        .andExpect(status().isNoContent());

    // 3. 로그인 시도 - 완전히 삭제된 계정으로 로그인 불가 확인
    UserLoginRequest loginRequest = new UserLoginRequest(
        "harddelete@test.com",
        "password123"
    );

    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("하드 삭제 실패 - 존재하지 않는 사용자")
  void hardDelete_Fail_NonExistentUser() throws Exception {
    // Given
    String nonExistentUserId = UUID.randomUUID().toString();

    // When & Then
    mockMvc.perform(delete("/api/users/" + nonExistentUserId + "/hard"))
        .andExpect(status().isNotFound()); // 404 Not Found 예상
  }

}