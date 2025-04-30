package com.sprint.monew.domain.interest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.interest.subscription.Subscription;
import com.sprint.monew.domain.user.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Import({CustomInterestRepositoryImpl.class, TestQuerydslConfig.class})
public class InterestRepositoryTest {

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

  @Autowired
  private EntityManager em;

  @Autowired
  private InterestRepository interestRepository;

  private User user;
  private Interest interest;

  @BeforeEach
  void setUp() {
    //사용자
    String email = "test@example.com";
    String nickname = "테스트유저";
    String password = "test1234";
    Instant createdAt = Instant.now();
    boolean deleted = false;
    user = new User(email, nickname, password, createdAt, deleted);
    em.persist(user);

    //관심사
    String name = "기술";
    List<String> keywords = List.of("인공지능", "AI", "IT", "프로그래밍");
    interest = new Interest(name, keywords);
    em.persist(interest);

    //구독
    Subscription subscription = new Subscription(user, interest);
    em.persist(subscription);

  }

  @Test
  @DisplayName("동일 이름 관심사 존재 여부 반환")
  void existsByName() {
    //given
    String keyword = "기술";

    //when & then
    assertThat(interestRepository.existsByName(keyword)).isTrue();

  }

  @Test
  @DisplayName("키워드 검색 총 결과 수 반환")
  void countByKeyword() {
    //given
    String name = "프로그래밍";
    List<String> keywords = List.of("개발자", "AI", "IT", "프론트엔드", "백엔드");
    interest = new Interest(name, keywords);
    em.persist(interest);

    String searchKeyword = "AI";

    //when & then
    assertThat(interestRepository.countByKeyword(searchKeyword)).isEqualTo(2);
  }
}
