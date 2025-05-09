package com.sprint.monew.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.monew.MongoContainer;
import com.sprint.monew.PostgresContainer;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("활동내역 통합 테스트")
@Sql(scripts = "/seed-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserActivityIntegrationTest {

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

  UUID activeUserId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  UUID nonActiveUserId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");


  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    // MongoDB 연결
    registry.add(
        "spring.data.mongodb.uri",
        MongoContainer.getInstance()::getReplicaSetUrl);

    // PostgreSQL 연결
    PostgresContainer postgres = PostgresContainer.getInstance();
    registry.add(
        "spring.datasource.url",
        postgres::getJdbcUrl);
    registry.add(
        "spring.datasource.username",
        postgres::getUsername);
    registry.add(
        "spring.datasource.password",
        postgres::getPassword);
    registry.add(
        "spring.datasource.driver-class-name",
        () -> "org.postgresql.Driver");
    registry.add(
        "spring.jpa.hibernate.ddl-auto",
        () -> "create");
  }


  @Test
  @DisplayName("활동내역을 MongoDB에 저장한다")
  void saveUserActivityToMongo() throws
      Exception {
    // Given
    String url = String.format(
        "/api/user-activities/%s/save",
        activeUserId);

    // When & Then
    mockMvc.perform(get(url))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("MongoDB에서 활동내역을 조회한다")
  void getUserActivityFromMongo() throws
      Exception {
    // Given
    String url = String.format(
        "/api/user-activities/%s",
        activeUserId);

    // When & Then
    mockMvc.perform(get(url))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("기사를 조회한 후 조회내역이 업데이트 된다")
  void getUserActivityFromRDB() throws
      Exception {
    // Given
    String articleURL = "/api/articles?orderBy=createdAt&direction=DESC&limit=1";

    // When & Then
    String articleResponse = mockMvc.perform(get(articleURL).header(
            "Monew-Request-User-ID",
            nonActiveUserId))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    JsonNode articleResponseNode = new ObjectMapper().readTree(articleResponse);
    String articleId = articleResponseNode.get("content")
        .get(0)
        .get("id")
        .asText();

    mockMvc.perform(get(
            "/api/user-activities/{id}/save",
            nonActiveUserId))
        .andExpect(status().isOk());

    String activityResponseBefore = mockMvc.perform(get(
            "/api/user-activities/{id}",
            nonActiveUserId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(nonActiveUserId.toString()))
        .andReturn()
        .getResponse()
        .getContentAsString();
    JsonNode activityResponseNodeBefore = new ObjectMapper().readTree(activityResponseBefore);
    int articleViewCountBefore = activityResponseNodeBefore.get("articleViews").size();

    String articleViewURL = String.format(
        "/api/articles/%s/article-views",
        articleId);

    mockMvc.perform(post(articleViewURL)
            .header("Monew-Request-User-ID", nonActiveUserId))
        .andExpect(status().isOk());

    String activityResponseAfter = mockMvc.perform(get(
            "/api/user-activities/{id}",
            nonActiveUserId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(nonActiveUserId.toString()))
        .andReturn()
        .getResponse()
        .getContentAsString();
    JsonNode activityResponseNodeAfter = new ObjectMapper().readTree(activityResponseAfter);
    int articleViewCountAfter = activityResponseNodeAfter.get("articleViews").size();

    assertEquals(articleViewCountBefore + 1, articleViewCountAfter);
  }
}
