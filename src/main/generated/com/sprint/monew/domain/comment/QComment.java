package com.sprint.monew.domain.comment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1667480291L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final com.sprint.monew.domain.article.QArticle article;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final BooleanPath deleted = createBoolean("deleted");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<com.sprint.monew.domain.comment.like.Like, com.sprint.monew.domain.comment.like.QLike> likes = this.<com.sprint.monew.domain.comment.like.Like, com.sprint.monew.domain.comment.like.QLike>createList("likes", com.sprint.monew.domain.comment.like.Like.class, com.sprint.monew.domain.comment.like.QLike.class, PathInits.DIRECT2);

    public final com.sprint.monew.domain.user.QUser user;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.sprint.monew.domain.article.QArticle(forProperty("article")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint.monew.domain.user.QUser(forProperty("user")) : null;
    }

}

