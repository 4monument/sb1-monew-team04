package com.sprint.monew.domain.interest.userinterest;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserInterestKey is a Querydsl query type for UserInterestKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserInterestKey extends BeanPath<UserInterestKey> {

    private static final long serialVersionUID = 1085495778L;

    public static final QUserInterestKey userInterestKey = new QUserInterestKey("userInterestKey");

    public final ComparablePath<java.util.UUID> interestId = createComparable("interestId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QUserInterestKey(String variable) {
        super(UserInterestKey.class, forVariable(variable));
    }

    public QUserInterestKey(Path<? extends UserInterestKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserInterestKey(PathMetadata metadata) {
        super(UserInterestKey.class, metadata);
    }

}

