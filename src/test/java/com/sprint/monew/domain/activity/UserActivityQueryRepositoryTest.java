package com.sprint.monew.domain.activity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({UserActivityQueryRepository.class})
class UserActivityQueryRepositoryTest {
    @Autowired
    private UserActivityQueryRepository repository;

    @Test
    @DisplayName("사용자_활동_조회")
    void findUserActivity() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        UserActivityDto result = repository.findUserActivity(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
    }
}