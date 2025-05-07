package com.sprint.monew.domain.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.monew.MongoContainer;
import com.sprint.monew.PostgresContainer;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
@DisplayName("알림 API 통합 테스트")
@Sql(scripts = "/seed-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class NotificationIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  static final PostgresContainer postgresContainer = PostgresContainer.getInstance();
  static final MongoContainer mongoContainer = MongoContainer.getInstance();

  static {
    postgresContainer.start();
    mongoContainer.start();
  }

  @DynamicPropertySource
  static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);

    registry.add("spring.data.mongodb.uri", MongoContainer.getInstance()::getReplicaSetUrl);
  }

  UUID activeUserId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  UUID nonActiveUserId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

  UUID notificationForActiveUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
  UUID notificationForNonActiveUserId = UUID.fromString("987e6543-e21b-12d3-b456-426614174000");

  @Nested
  @DisplayName("알림 전체 조회")
  class getNotifications {

    @Test
    @DisplayName("성공")
    void getNotificationsSuccess() throws Exception {
      mockMvc.perform(get("/api/notifications", activeUserId.toString())
              .header("Monew-Request-User-ID", activeUserId.toString()))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("실패: 해당ID를 가진 사용자가 없음")
    void getNotificationsFailure() throws Exception {
      UUID testUserId = UUID.randomUUID();
      mockMvc.perform(get("/api/notifications", testUserId.toString())
              .header("Monew-Request-User-ID", testUserId.toString()))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("알림 전체 확인(수정)")
  class checkAllNotifications {

    @Test
    @DisplayName("성공")
    void checkAllNotificationsSuccess() throws Exception {
      mockMvc.perform(patch("/api/notifications")
              .header("Monew-Request-User-ID", activeUserId.toString()))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("실패")
    void checkAllNotificationsFailure() throws Exception {
      UUID testUserId = UUID.randomUUID();
      mockMvc.perform(patch("/api/notifications")
              .header("Monew-Request-User-ID", testUserId))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("알림 단일 확인(수정)")
  class checkNotification {

    @Test
    @DisplayName("성공")
    void checkNotificationSuccess() throws Exception {
      mockMvc.perform(
              patch("/api/notifications/" + notificationForActiveUserId)
                  .header("Monew-Request-User-ID", activeUserId.toString()))
          .andExpect(status().isOk());

    }

    @Test
    @DisplayName("실패: 해당 ID를 가진 사용자가 없음")
    void checkNotificationFailureSinceUserId() throws Exception {
      UUID randomUUID = UUID.randomUUID();
      mockMvc.perform(patch("/api/notifications/" + randomUUID)
              .header("Monew-Request-User-ID", randomUUID))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 해당 ID를 가진 알림이 없음")
    void checkNotificationFailureSinceNotificationId() throws Exception {
      UUID randomUUID = UUID.randomUUID();
      mockMvc.perform(patch("/api/notifications/" + randomUUID)
              .header("Monew-Request-User-ID", randomUUID))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 해당 사용자의 알림이 아님")
    void checkNotificationFailureNotOwner() throws Exception {
      mockMvc.perform(patch("/api/notifications/" + notificationForActiveUserId)
              .header("Monew-Request-User-ID", notificationForActiveUserId.toString()))
          .andExpect(status().isNotFound());
    }
  }
}
