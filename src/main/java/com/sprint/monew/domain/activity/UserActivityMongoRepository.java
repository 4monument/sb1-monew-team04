package com.sprint.monew.domain.activity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserActivityMongoRepository extends MongoRepository<UserActivityDocument, UUID> {

}
