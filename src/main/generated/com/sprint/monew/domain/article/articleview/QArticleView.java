package com.sprint.monew.domain.article.articleview;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleView is a Querydsl query type for ArticleView
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleView extends EntityPathBase<ArticleView> {

    private static final long serialVersionUID = -1207859139L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleView articleView = new QArticleView("articleView");

    public final com.sprint.monew.domain.article.QArticle article;

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final QArticleViewKey id;

    public final com.sprint.monew.domain.user.QUser user;

    public QArticleView(String variable) {
        this(ArticleView.class, forVariable(variable), INITS);
    }

    public QArticleView(Path<? extends ArticleView> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleView(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleView(PathMetadata metadata, PathInits inits) {
        this(ArticleView.class, metadata, inits);
    }

    public QArticleView(Class<? extends ArticleView> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.sprint.monew.domain.article.QArticle(forProperty("article")) : null;
        this.id = inits.isInitialized("id") ? new QArticleViewKey(forProperty("id")) : null;
        this.user = inits.isInitialized("user") ? new com.sprint.monew.domain.user.QUser(forProperty("user")) : null;
    }

}

