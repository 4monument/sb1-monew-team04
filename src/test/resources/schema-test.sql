CREATE TABLE "users"
(
    "id"         UUID PRIMARY KEY,
    "email"      VARCHAR(255) NOT NULL UNIQUE,
    "nickname"   VARCHAR(255) NOT NULL,
    "password"   VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMPTZ  NOT NULL DEFAULT now(),
    "deleted"    BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE "articles"
(
    "id"           UUID PRIMARY KEY,
    "source"       VARCHAR(255)  NOT NULL,
    "source_url"   VARCHAR(2048) NOT NULL UNIQUE,
    "title"        VARCHAR(255)  NOT NULL,
    "publish_date" TIMESTAMPTZ   NOT NULL DEFAULT now(),
    "summary"      VARCHAR(255)  NOT NULL,
    "deleted"      BOOLEAN       NOT NULL DEFAULT FALSE
);

CREATE TABLE "comments"
(
    "id"         UUID PRIMARY KEY,
    "user_id"    UUID         NOT NULL,
    "article_id" UUID         NOT NULL,
    "content"    VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMPTZ  NOT NULL DEFAULT now(),
    "deleted"    BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE "notifications"
(
    "id"            UUID PRIMARY KEY,
    "user_id"       UUID         NOT NULL,
    "resource_id"   UUID         NOT NULL,
    "resource_type" VARCHAR(50)  NOT NULL,
    "content"       VARCHAR(255) NOT NULL,
    "created_at"    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    "updated_at"    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    "confirmed"     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE "interests"
(
    "id"         UUID PRIMARY KEY,
    "name"       VARCHAR(50) NOT NULL UNIQUE,
    "keywords"   JSONB       NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE "users_interests"
(
    "id"          UUID PRIMARY KEY,
    "user_id"     UUID,
    "interest_id" UUID,
    "created_at"  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, interest_id)
);

CREATE TABLE "articles_views"
(
    "id"         UUID PRIMARY KEY,
    "user_id"    UUID,
    "article_id" UUID,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, article_id)
);

CREATE TABLE "likes"
(
    "id"         UUID PRIMARY KEY,
    "comment_id" UUID        NOT NULL,
    "user_id"    UUID        NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE "articles_interests"
(
    "id"          UUID PRIMARY KEY,
    "article_id"  UUID NOT NULL,
    "interest_id" UUID NOT NULL,
    UNIQUE (article_id, interest_id)
);

ALTER TABLE "comments"
    ADD CONSTRAINT "FK_user_TO_comment_1"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON DELETE SET NULL;

ALTER TABLE "comments"
    ADD CONSTRAINT "FK_article_TO_comment_1"
        FOREIGN KEY ("article_id")
            REFERENCES "articles" ("id")
            ON DELETE CASCADE;

ALTER TABLE "notifications"
    ADD CONSTRAINT "FK_user_TO_notification_1"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON DELETE CASCADE;

ALTER TABLE "users_interests"
    ADD CONSTRAINT "FK_user_TO_user_interest_1"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON DELETE CASCADE;

ALTER TABLE "users_interests"
    ADD CONSTRAINT "FK_interest_TO_user_interest_1"
        FOREIGN KEY ("interest_id")
            REFERENCES "interests" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_views"
    ADD CONSTRAINT "FK_user_TO_article_views_1"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_views"
    ADD CONSTRAINT "FK_article_TO_article_views_1"
        FOREIGN KEY ("article_id")
            REFERENCES "articles" ("id")
            ON DELETE CASCADE;

ALTER TABLE "likes"
    ADD CONSTRAINT "FK_comment_TO_likes_1"
        FOREIGN KEY ("comment_id")
            REFERENCES "comments" ("id")
            ON DELETE CASCADE;

ALTER TABLE "likes"
    ADD CONSTRAINT "FK_user_TO_likes_1"
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_interests"
    ADD CONSTRAINT "FK_article_TO_articles_interest_1"
        FOREIGN KEY ("article_id")
            REFERENCES "articles" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_interests"
    ADD CONSTRAINT "FK_interest_TO_articles_interest_1"
        FOREIGN KEY ("interest_id")
            REFERENCES "interests" ("id")
            ON DELETE CASCADE;

create table articles
(
    deleted      boolean default false       not null,
    publish_date timestamp(6) with time zone not null,
    id           uuid                        not null
        primary key,
    source_url   varchar(2048)               not null
        unique,
    source       varchar(255)                not null,
    summary      varchar(255)                not null,
    title        varchar(255)                not null
);

alter table articles
    owner to monew;

create table interests
(
    id       uuid         not null
        primary key,
    keywords jsonb,
    name     varchar(255) not null
);

alter table interests
    owner to monew;

create table users
(
    deleted    boolean default false    not null,
    created_at timestamp with time zone not null,
    id         uuid                     not null
        primary key,
    email      varchar(255)             not null
        unique,
    nickname   varchar(255)             not null,
    password   varchar(255)             not null
);

alter table users
    owner to monew;

create table comments
(
    deleted    boolean default false       not null,
    created_at timestamp(6) with time zone not null,
    article_id uuid
        constraint fkk4ib6syde10dalk7r7xdl0m5p
            references articles,
    id         uuid                        not null
        primary key,
    user_id    uuid
        constraint fk8omq0tc18jd43bu5tjh6jvraq
            references users,
    content    varchar(255)                not null
);

alter table comments
    owner to monew;

create table notifications
(
    confirmed     boolean                     not null,
    created_at    timestamp(6) with time zone not null,
    updated_at    timestamp(6) with time zone not null,
    id            uuid                        not null
        primary key,
    resource_id   uuid                        not null,
    user_id       uuid                        not null
        constraint fk9y21adhxn0ayjhfocscqox7bh
            references users,
    content       varchar(255)                not null,
    resource_type varchar(255)                not null
        constraint notifications_resource_type_check
            check ((resource_type)::text = ANY
                   ((ARRAY ['INTEREST'::character varying, 'COMMENT'::character varying])::text[]))
);

alter table notifications
    owner to monew;

