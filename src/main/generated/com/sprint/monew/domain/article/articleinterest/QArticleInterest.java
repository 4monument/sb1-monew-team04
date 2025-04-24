package com.sprint.monew.domain.article.articleinterest;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleInterest is a Querydsl query type for ArticleInterest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleInterest extends EntityPathBase<ArticleInterest> {

    private static final long serialVersionUID = -2000536121L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleInterest articleInterest = new QArticleInterest("articleInterest");

    public final com.sprint.monew.domain.article.QArticle article;

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.sprint.monew.domain.interest.QInterest interest;

    public QArticleInterest(String variable) {
        this(ArticleInterest.class, forVariable(variable), INITS);
    }

    public QArticleInterest(Path<? extends ArticleInterest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleInterest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleInterest(PathMetadata metadata, PathInits inits) {
        this(ArticleInterest.class, metadata, inits);
    }

    public QArticleInterest(Class<? extends ArticleInterest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.sprint.monew.domain.article.QArticle(forProperty("article")) : null;
        this.interest = inits.isInitialized("interest") ? new com.sprint.monew.domain.interest.QInterest(forProperty("interest")) : null;
    }

}

