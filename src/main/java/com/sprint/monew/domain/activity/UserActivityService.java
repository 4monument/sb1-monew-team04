package com.sprint.monew.domain.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityQueryRepository userActivityQueryRepository;

    public UserActivityDto getUserActivity(UUID userId) {
        return userActivityQueryRepository.findUserActivity(userId);
    }
}
