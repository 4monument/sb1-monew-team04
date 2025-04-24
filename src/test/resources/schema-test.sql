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



-- we don't know how to generate root <with-no-name> (class Root) :(

comment on database postgres is 'default administrative connection database';

create sequence batch_step_execution_seq;

alter sequence batch_step_execution_seq owner to monew;

create sequence batch_job_execution_seq;

alter sequence batch_job_execution_seq owner to monew;

create sequence batch_job_seq;

alter sequence batch_job_seq owner to monew;

create table batch_job_instance
(
    job_instance_id bigint       not null
        primary key,
    version         bigint,
    job_name        varchar(100) not null,
    job_key         varchar(32)  not null,
    constraint job_inst_un
        unique (job_name, job_key)
);

alter table batch_job_instance
    owner to monew;

create table batch_job_execution
(
    job_execution_id bigint    not null
        primary key,
    version          bigint,
    job_instance_id  bigint    not null
        constraint job_inst_exec_fk
            references batch_job_instance,
    create_time      timestamp not null,
    start_time       timestamp,
    end_time         timestamp,
    status           varchar(10),
    exit_code        varchar(2500),
    exit_message     varchar(2500),
    last_updated     timestamp
);

alter table batch_job_execution
    owner to monew;

create table batch_job_execution_params
(
    job_execution_id bigint       not null
        constraint job_exec_params_fk
            references batch_job_execution,
    parameter_name   varchar(100) not null,
    parameter_type   varchar(100) not null,
    parameter_value  varchar(2500),
    identifying      char         not null
);

alter table batch_job_execution_params
    owner to monew;

create table batch_step_execution
(
    step_execution_id  bigint       not null
        primary key,
    version            bigint       not null,
    step_name          varchar(100) not null,
    job_execution_id   bigint       not null
        constraint job_exec_step_fk
            references batch_job_execution,
    create_time        timestamp    not null,
    start_time         timestamp,
    end_time           timestamp,
    status             varchar(10),
    commit_count       bigint,
    read_count         bigint,
    filter_count       bigint,
    write_count        bigint,
    read_skip_count    bigint,
    write_skip_count   bigint,
    process_skip_count bigint,
    rollback_count     bigint,
    exit_code          varchar(2500),
    exit_message       varchar(2500),
    last_updated       timestamp
);

alter table batch_step_execution
    owner to monew;

create table batch_step_execution_context
(
    step_execution_id  bigint        not null
        primary key
        constraint step_exec_ctx_fk
            references batch_step_execution,
    short_context      varchar(2500) not null,
    serialized_context text
);

alter table batch_step_execution_context
    owner to monew;

create table batch_job_execution_context
(
    job_execution_id   bigint        not null
        primary key
        constraint job_exec_ctx_fk
            references batch_job_execution,
    short_context      varchar(2500) not null,
    serialized_context text
);

alter table batch_job_execution_context
    owner to monew;

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

