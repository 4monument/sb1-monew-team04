package com.sprint.monew;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class MonewApplicationTests {

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
	void contextLoads() {
	}

}
