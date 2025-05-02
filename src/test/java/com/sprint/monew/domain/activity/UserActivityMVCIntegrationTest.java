package com.sprint.monew.domain.activity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.monew.MongoContainer;
import com.sprint.monew.PostgresContainer;
import com.sprint.monew.domain.article.repository.ArticleRepository;
import com.sprint.monew.domain.interest.InterestRepository;
import com.sprint.monew.domain.user.User;
import com.sprint.monew.domain.user.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
class UserActivityMVCIntegrationTest {

  @LocalServerPort
  private int port;

  @BeforeAll
  static void beforeAll() {
    MongoContainer.getInstance().start();
    PostgresContainer.getInstance().start();
  }
  @Autowired
  private MockMvc mockMvc;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    // MongoDB 연결
    registry.add("spring.data.mongodb.uri", MongoContainer.getInstance()::getReplicaSetUrl);

    // PostgreSQL 연결
    PostgresContainer postgres = PostgresContainer.getInstance();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
  }

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private InterestRepository interestRepository;

  @Autowired
  private ArticleRepository articleRepository;

  private UUID testUserId;

  @BeforeEach
  void setUp() {
    // 사용자 및 샘플 데이터 저장
    User user = new User(
        "test@email.com",
        "nickname",
        "password",
        Instant.now(),
        false);
    userRepository.save(user);

    testUserId = user.getId();
  }

  @Test
  @DisplayName("성공 - 유저의 모든 활동 정보를 RDB에서 조회한다")
  void testGetUserActivityFromQueryDb() throws Exception {
    mockMvc.perform(get("/api/user-activities/{userId}/query", testUserId)
            .header("Monew-Request-User-ID", testUserId))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 유저의 정보를 RDB에서 조회한다")
  void testGetNonExistsUserActivityFromQueryDb() throws Exception {
    UUID nonExistentUserId = UUID.randomUUID();
    mockMvc.perform(get("/api/user-activities/{userId}/query", nonExistentUserId)
            .header("Monew-Request-User-ID", nonExistentUserId))
        .andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("성공 - 유저의 모든 활동 정보를 MongoDB에 저장한다")
  void testSaveUserActivityToMongo() throws Exception {
    mockMvc.perform(get("/api/user-activities/{userId}/save", testUserId)
            .header("Monew-Request-User-ID", testUserId))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 유저의 정보를 MongoDB에 저장한다")
  void testSaveNonExistsUserActivityToMongo() throws Exception {
    UUID nonExistentUserId = UUID.randomUUID();
    mockMvc.perform(get("/api/user-activities/{userId}/save", nonExistentUserId)
            .header("Monew-Request-User-ID", nonExistentUserId))
        .andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("실패 - mongoDB엔 유저가 존재하지 않지만 조회한다")
  void testGetNonExistUserActivityFromMongo() throws Exception {
    mockMvc.perform(get("/api/user-activities/{userId}", testUserId)
            .header("Monew-Request-User-ID", testUserId))
        .andExpect(status().is4xxClientError());
//        .andExpect(jsonPath("$.userId").value(testUserId.toString()));
  }

  @Test
  @DisplayName("성공 - 유저의 모든 활동 정보를 MongoDB에서 조회한다")
  void testGetUserActivityFromMongo() throws Exception {
    mockMvc.perform(get("/api/user-activities/{userId}/save", testUserId)
            .header("Monew-Request-User-ID", testUserId))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/user-activities/{userId}", testUserId)
            .header("Monew-Request-User-ID", testUserId))
        .andExpect(status().isOk());
  }
}
