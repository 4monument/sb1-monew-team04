package com.sprint.monew.domain.activity;

import com.sprint.monew.domain.activity.exception.UserActivityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserActivityService {

  private final UserActivityQueryRepository userActivityQueryRepository;
  private final UserActivityMongoRepository userActivityMongoRepository;

  @Cacheable(value = "userActivities", key = "#userId")
  public UserActivityDto getUserActivity(UUID userId) {
    return userActivityQueryRepository.findUserActivity(userId);
  }

  @Cacheable(value = "userActivities", key = "#userId")
  public UserActivityDto getUserActivityFromMongo(UUID userId) {
    UserActivityDocument document = userActivityMongoRepository.findById(userId)
        .orElseThrow(() -> UserActivityNotFoundException.withId(userId));
    return UserActivityDto.fromDocument(document);
  }

  @Transactional
  public UserActivityDto synchronizeUserActivityToMongo(UUID userId) {
    UserActivityDto dto = userActivityQueryRepository.findUserActivity(userId);
    UserActivityDocument document = UserActivityDto.toDocument(dto);
    userActivityMongoRepository.save(document);
    return dto;
  }

  @Transactional
  public UserActivityDto saveUserActivityToMongo(UUID userId) {
    return synchronizeUserActivityToMongo(userId);
  }

  @Transactional
  public void updateUserActivity(UUID userId) {
    synchronizeUserActivityToMongo(userId);
  }
}
