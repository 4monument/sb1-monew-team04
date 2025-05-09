package com.sprint.monew.domain.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.monew.MongoContainer;
import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.util.CursorPageResponseDto;
import com.sprint.monew.domain.article.dto.ArticleDto;
import java.util.List;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Testcontainers
@Sql(scripts = "/seed-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ArticleIntegrationTest {

  static final PostgresContainer postgresContainer = PostgresContainer.getInstance();
  static final MongoContainer mongoContainer = MongoContainer.getInstance();

  static {
    postgresContainer.start();
    mongoContainer.start();
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  String baseUrl = "/api/articles";
  String monewUserHeader = "Monew-Request-User-ID";
  UUID userId = UUID.randomUUID();

  @DynamicPropertySource
  static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);

    registry.add("spring.data.mongodb.uri", MongoContainer.getInstance()::getReplicaSetUrl);
  }

  @Test
  @DisplayName("article 조회 - publishDate로 내림차순 정렬")
  public void publishDateDesc() throws Exception {
    String url = baseUrl + "?orderBy=publishDate&direction=DESC&limit=2";
    MvcResult mvcResult = mockMvc.perform(get(url)
            .header(monewUserHeader, userId))
        .andExpect(status().isOk())
        .andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();
    CursorPageResponseDto<ArticleDto> response = objectMapper.readValue(
        contentAsString, new TypeReference<>() {});
    List<ArticleDto> content = response.content();

    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isEqualTo("2025-05-06T00:00:00Z");
    assertThat(response.totalElements()).isEqualTo(7);
    assertThat(content).hasSize(2);
    assertThat(content).extracting("title")
        .containsExactly("정신 건강의 중요성", "영양소의 균형과 장수");
  }

  @Test
  @DisplayName("article 조회 - publishDate로 오름차순 정렬")
  public void publishDateAsc() throws Exception {
    String url = baseUrl + "?orderBy=publishDate&direction=ASC&limit=2";
    MvcResult mvcResult = mockMvc.perform(get(url)
            .header(monewUserHeader, userId))
        .andExpect(status().isOk())
        .andReturn();

    String contentAsString = mvcResult.getResponse().getContentAsString();
    CursorPageResponseDto<ArticleDto> response = objectMapper.readValue(
        contentAsString, new TypeReference<>() {});
    List<ArticleDto> content = response.content();

    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isEqualTo("2025-05-02T00:00:00Z");
    assertThat(response.totalElements()).isEqualTo(7);
    assertThat(content).hasSize(2);
    assertThat(content).extracting("title")
        .containsExactly("인공지능의 미래", "건강하게 사는 법");
  }

}
