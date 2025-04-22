package com.sprint.monew.domain.article;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QArticle is a Querydsl query type for Article
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticle extends EntityPathBase<Article> {

    private static final long serialVersionUID = -828277941L;

    public static final QArticle article = new QArticle("article");

    public final BooleanPath deleted = createBoolean("deleted");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.Instant> publishDate = createDateTime("publishDate", java.time.Instant.class);

    public final EnumPath<Article.Source> source = createEnum("source", Article.Source.class);

    public final StringPath sourceUrl = createString("sourceUrl");

    public final StringPath summary = createString("summary");

    public final StringPath title = createString("title");

    public QArticle(String variable) {
        super(Article.class, forVariable(variable));
    }

    public QArticle(Path<? extends Article> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticle(PathMetadata metadata) {
        super(Article.class, metadata);
    }

}

