package com.sprint.monew.domain.activity;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityService {

  private final UserActivityQueryRepository userActivityQueryRepository;
  private final UserActivityMongoRepository userActivityMongoRepository;

  public UserActivityDto getUserActivity(UUID userId) {
    return userActivityQueryRepository.findUserActivity(userId);
  }

  public UserActivityDto getUserActivityFromMongo(UUID userId) {
    UserActivityDocument document = userActivityMongoRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found in MongoDB: " + userId));

    return UserActivityDto.fromDocument(document);
  }

  public UserActivityDto saveUserActivityToMongo(UUID userId) {
    UserActivityDto dto = userActivityQueryRepository.findUserActivity(userId);
    UserActivityDocument document = UserActivityDto.toDocument(dto);
    userActivityMongoRepository.save(document);

    return dto;
  }

  public void updateUserActivity(UUID userId) {
    UserActivityDto dto = userActivityQueryRepository.findUserActivity(userId);
    UserActivityDocument document = UserActivityDto.toDocument(dto);
    userActivityMongoRepository.save(document);
  }
}
