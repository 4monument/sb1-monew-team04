package com.sprint.monew.domain.article.articleview;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QArticleViewKey is a Querydsl query type for ArticleViewKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QArticleViewKey extends BeanPath<ArticleViewKey> {

    private static final long serialVersionUID = -95528734L;

    public static final QArticleViewKey articleViewKey = new QArticleViewKey("articleViewKey");

    public final ComparablePath<java.util.UUID> articleId = createComparable("articleId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QArticleViewKey(String variable) {
        super(ArticleViewKey.class, forVariable(variable));
    }

    public QArticleViewKey(Path<? extends ArticleViewKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticleViewKey(PathMetadata metadata) {
        super(ArticleViewKey.class, metadata);
    }

}

