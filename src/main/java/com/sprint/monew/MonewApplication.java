package com.sprint.monew;

import com.sprint.monew.domain.activity.UserActivityMongoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableMongoRepositories(basePackageClasses = UserActivityMongoRepository.class)
public class MonewApplication {

  public static void main(String[] args) {
    SpringApplication.run(MonewApplication.class, args);
  }

}
