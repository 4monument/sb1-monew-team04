package com.sprint.monew.domain.activity;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityMongoRepository extends MongoRepository<UserActivityDocument, UUID> {

}
