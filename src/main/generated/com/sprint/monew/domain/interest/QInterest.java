package com.sprint.monew.domain.interest;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterest is a Querydsl query type for Interest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInterest extends EntityPathBase<Interest> {

    private static final long serialVersionUID = 159591867L;

    public static final QInterest interest = new QInterest("interest");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<String, StringPath> keywords = this.<String, StringPath>createList("keywords", String.class, StringPath.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public QInterest(String variable) {
        super(Interest.class, forVariable(variable));
    }

    public QInterest(Path<? extends Interest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInterest(PathMetadata metadata) {
        super(Interest.class, metadata);
    }

}

