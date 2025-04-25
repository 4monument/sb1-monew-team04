package com.sprint.monew.integration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.monew.PostgresContainer;
import com.sprint.monew.domain.interest.dto.InterestCreateRequest;
import com.sprint.monew.domain.interest.dto.InterestUpdateRequest;
import com.sprint.monew.domain.user.UserRegisterRequest;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("관심사 API 통합 테스트")
public class InterestIntegrationTest {

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

  @Nested
  @DisplayName("관심사 등록")
  class interestRegister {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      InterestCreateRequest request = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(request);

      //when & then
      mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.name", is("음악/예술")))
          .andExpect(jsonPath("$.keywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")))
          .andExpect(jsonPath("$.subscriberCount", notNullValue()))
          .andExpect(jsonPath("$.subscribedByMe", notNullValue()));
    }

    @Test
    @DisplayName("실패: 유사한 이름 관심사 존재")
    void failureSinceSimilarInterestExists() throws Exception {
      InterestCreateRequest firstRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );
      String firstRequestBody = objectMapper.writeValueAsString(firstRequest);

      //when & then
      mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(firstRequestBody))
          .andExpect(status().isCreated());

      InterestCreateRequest secondRequest = new InterestCreateRequest(
          "예술/음악", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String secondRequestBody = objectMapper.writeValueAsString(secondRequest);

      mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(secondRequestBody))
          .andExpect(status().isConflict());
    }
  }

  @Nested
  @DisplayName("관심사 구독")
  class interestSubscribe {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      // 1. 사용자 등록
      UserRegisterRequest registerRequest = new UserRegisterRequest(
          "test@test.com",
          "testUser",
          "password1234"
      );

      MvcResult creatUserResult = mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(registerRequest)))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 사용자 ID 추출
      String responseJson = creatUserResult.getResponse().getContentAsString();
      String userId = objectMapper.readTree(responseJson).get("id").asText();

      // 3. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 4. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      //when & then
      mockMvc.perform(post("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.interestId", is(interestId)))
          .andExpect(jsonPath("$.interestName", is("음악/예술")))
          .andExpect(jsonPath("$.interestKeywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")))
          .andExpect(jsonPath("$.createdAt", notNullValue()))
          .andExpect(jsonPath("$.interestSubscriberCount", notNullValue()));
    }

    @Test
    @DisplayName("실패: 해당 ID의 사용자가 존재하지 않음")
    void failureSinceUserId() throws Exception {
      //given
      UUID randomUserUUID = UUID.randomUUID();

      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 4. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      //when & then
      mockMvc.perform(post("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", randomUserUUID))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 해당 ID의 관심사가 존재하지 않음")
    void failureSinceInterestId() throws Exception {
      //given
      // 1. 사용자 등록
      UserRegisterRequest registerRequest = new UserRegisterRequest(
          "test@test.com",
          "testUser",
          "password1234"
      );

      MvcResult creatUserResult = mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(registerRequest)))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 사용자 ID 추출
      String responseJson = creatUserResult.getResponse().getContentAsString();
      String userId = objectMapper.readTree(responseJson).get("id").asText();

      UUID randomInterestUUID = UUID.randomUUID();

      //when & then
      mockMvc.perform(post("/api/interests/" + randomInterestUUID + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("관심사 구독 취소")
  class interestUnsubscribe {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      // 1. 사용자 등록
      UserRegisterRequest registerRequest = new UserRegisterRequest(
          "test@test.com",
          "testUser",
          "password1234"
      );

      MvcResult creatUserResult = mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(registerRequest)))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 사용자 ID 추출
      String responseJson = creatUserResult.getResponse().getContentAsString();
      String userId = objectMapper.readTree(responseJson).get("id").asText();

      // 3. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 4. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      // 5. 관심사 구독
      mockMvc.perform(post("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.interestId", is(interestId)))
          .andExpect(jsonPath("$.interestName", is("음악/예술")))
          .andExpect(jsonPath("$.interestKeywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")))
          .andExpect(jsonPath("$.createdAt", notNullValue()))
          .andExpect(jsonPath("$.interestSubscriberCount", notNullValue()));

      //when&then
      mockMvc.perform(delete("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("실패: 해당 ID의 사용자가 존재하지 않음")
    void failureSinceUserId() throws Exception {
      //given
      // 1. 사용자 등록
      UserRegisterRequest registerRequest = new UserRegisterRequest(
          "test@test.com",
          "testUser",
          "password1234"
      );

      MvcResult creatUserResult = mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(registerRequest)))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 사용자 ID 추출
      String responseJson = creatUserResult.getResponse().getContentAsString();
      String userId = objectMapper.readTree(responseJson).get("id").asText();

      // 3. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 4. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      // 5. 관심사 구독
      mockMvc.perform(post("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.interestId", is(interestId)))
          .andExpect(jsonPath("$.interestName", is("음악/예술")))
          .andExpect(jsonPath("$.interestKeywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")))
          .andExpect(jsonPath("$.createdAt", notNullValue()))
          .andExpect(jsonPath("$.interestSubscriberCount", notNullValue()));

      // 6. 잘못된 userID
      UUID randomUUID = UUID.randomUUID();

      //when&then
      mockMvc.perform(delete("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", randomUUID))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 해당 ID의 관심사가 존재하지 않음")
    void failureSinceInterestId() throws Exception {
      //given
      // 1. 사용자 등록
      UserRegisterRequest registerRequest = new UserRegisterRequest(
          "test@test.com",
          "testUser",
          "password1234"
      );

      MvcResult creatUserResult = mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(registerRequest)))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 사용자 ID 추출
      String responseJson = creatUserResult.getResponse().getContentAsString();
      String userId = objectMapper.readTree(responseJson).get("id").asText();

      // 3. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 4. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      // 5. 관심사 구독
      mockMvc.perform(post("/api/interests/" + interestId + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.interestId", is(interestId)))
          .andExpect(jsonPath("$.interestName", is("음악/예술")))
          .andExpect(jsonPath("$.interestKeywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")))
          .andExpect(jsonPath("$.createdAt", notNullValue()))
          .andExpect(jsonPath("$.interestSubscriberCount", notNullValue()));

      // 6. 잘못된 관심사 ID
      UUID randomUUID = UUID.randomUUID();

      //when&then
      mockMvc.perform(delete("/api/interests/" + randomUUID + "/subscriptions")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Monew-Request-User-ID", userId))
          .andExpect(status().isNotFound());
    }

  }


  @Nested
  @DisplayName("관심사 수정")
  class interestModify {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      // 1. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      // 3. 요청값 준비
      InterestUpdateRequest updateRequest = new InterestUpdateRequest(
          Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );
      String updateRequestBody = objectMapper.writeValueAsString(updateRequest);

      //when & then
      mockMvc.perform(patch("/api/interests/" + interestId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(updateRequestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.name", is("음악/예술")))
          .andExpect(jsonPath("$.keywords", containsInAnyOrder("클래식", "재즈", "힙합", "현대미술")));
    }

    @Test
    @DisplayName("실패: 해당 ID의 관심사가 존재하지 않음")
    void failureSinceInterestId() throws Exception {
      UUID randomUUID = UUID.randomUUID();
      InterestUpdateRequest updateRequest = new InterestUpdateRequest(
          Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );
      String updateRequestBody = objectMapper.writeValueAsString(updateRequest);

      //when & then
      mockMvc.perform(patch("/api/interests/" + randomUUID)
              .contentType(MediaType.APPLICATION_JSON)
              .content(updateRequestBody))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("관심사 삭제")
  class interestDelete {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      // 1. 관심사 등록
      InterestCreateRequest createRequest = new InterestCreateRequest(
          "음악/예술", Arrays.asList("클래식", "재즈", "힙합", "현대미술")
      );

      String requestBody = objectMapper.writeValueAsString(createRequest);

      MvcResult createResult = mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isCreated())
          .andReturn();

      // 2. 생성된 관심사 ID 추출
      String responseInterestJson = createResult.getResponse().getContentAsString();
      String interestId = objectMapper.readTree(responseInterestJson).get("id").asText();

      //when & then
      mockMvc.perform(delete("/api/interests/" + interestId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent());

      mockMvc.perform(delete("/api/interests/" + interestId)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 해당 ID의 관심사가 존재하지 않음")
    void failureSinceInterestId() throws Exception {
      //given
      UUID randomUUID = UUID.randomUUID();

      //when & then
      mockMvc.perform(delete("/api/interests/" + randomUUID)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}
