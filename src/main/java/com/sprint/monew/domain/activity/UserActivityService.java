package com.sprint.monew.domain.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityQueryRepository userActivityQueryRepository;
    private final UserActivityMongoRepository userActivityMongoRepository;

    public UserActivityDto getUserActivity(UUID userId) {
        return userActivityQueryRepository.findUserActivity(userId);
    }
}
