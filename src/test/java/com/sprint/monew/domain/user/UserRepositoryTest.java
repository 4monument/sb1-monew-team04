package com.sprint.monew.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.monew.PostgresContainer;
import com.sprint.monew.common.config.TestQuerydslConfig;
import com.sprint.monew.domain.activity.UserActivityQueryRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureDataMongo
@Import({UserActivityQueryRepository.class, TestQuerydslConfig.class})
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager em;

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

  private User createTestUser(String email, String nickname) {
    return new User(
        null,
        email,
        nickname,
        "securePassword123",
        Instant.now(),
        false
    );
  }

  @Test
  @DisplayName("이메일로 존재 여부를 확인")
  void existsByEmail_existingEmail_returnsTrue() {
    // given
    User user = createTestUser("test@example.com", "tester");
    userRepository.save(user);
    em.flush();

    // when
    boolean exists = userRepository.existsByEmail("test@example.com");

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("이메일 + 삭제되지 않은 사용자 조회")
  void findByEmailAndDeletedFalse_existingActiveUser_returnsUser() {
    // given
    User user = createTestUser("active@example.com", "activeUser");
    userRepository.save(user);
    em.flush();

    // when
    Optional<User> found = userRepository.findByEmailAndDeletedFalse("active@example.com");

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("active@example.com");
  }

  @Test
  @DisplayName("삭제된 사용자는 조회 X")
  void findByEmailAndDeletedFalse_deletedUser_returnsEmpty() {
    // given
    User deletedUser = createTestUser("deleted@example.com", "ghost");
    deletedUser.markDeleted(); // 내부에서 deleted = true 설정
    userRepository.save(deletedUser);
    em.flush();

    // when
    Optional<User> result = userRepository.findByEmailAndDeletedFalse("deleted@example.com");

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("UUID로 삭제되지 않은 사용자 조회")
  void findByIdAndDeletedFalse_existingActiveUser_returnsUser() {
    // given
    User user = createTestUser("id@example.com", "uuidUser");
    userRepository.save(user);
    em.flush();
    UUID id = user.getId();

    // when
    Optional<User> result = userRepository.findByIdAndDeletedFalse(id);

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getNickname()).isEqualTo("uuidUser");
  }
}
