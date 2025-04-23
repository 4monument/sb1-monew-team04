package com.sprint.monew.domain.article.articleinterest;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QArticleInterestKey is a Querydsl query type for ArticleInterestKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QArticleInterestKey extends BeanPath<ArticleInterestKey> {

    private static final long serialVersionUID = -1005306088L;

    public static final QArticleInterestKey articleInterestKey = new QArticleInterestKey("articleInterestKey");

    public final ComparablePath<java.util.UUID> articleId = createComparable("articleId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> interestId = createComparable("interestId", java.util.UUID.class);

    public QArticleInterestKey(String variable) {
        super(ArticleInterestKey.class, forVariable(variable));
    }

    public QArticleInterestKey(Path<? extends ArticleInterestKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticleInterestKey(PathMetadata metadata) {
        super(ArticleInterestKey.class, metadata);
    }

}

