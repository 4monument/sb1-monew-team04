package com.sprint.monew.domain.interest.userinterest;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserInterest is a Querydsl query type for UserInterest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInterest extends EntityPathBase<UserInterest> {

    private static final long serialVersionUID = -1633409219L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserInterest userInterest = new QUserInterest("userInterest");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final QUserInterestKey id;

    public final com.sprint.monew.domain.interest.QInterest interest;

    public final com.sprint.monew.domain.user.QUser user;

    public QUserInterest(String variable) {
        this(UserInterest.class, forVariable(variable), INITS);
    }

    public QUserInterest(Path<? extends UserInterest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserInterest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserInterest(PathMetadata metadata, PathInits inits) {
        this(UserInterest.class, metadata, inits);
    }

    public QUserInterest(Class<? extends UserInterest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QUserInterestKey(forProperty("id")) : null;
        this.interest = inits.isInitialized("interest") ? new com.sprint.monew.domain.interest.QInterest(forProperty("interest")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint.monew.domain.user.QUser(forProperty("user")) : null;
    }

}

