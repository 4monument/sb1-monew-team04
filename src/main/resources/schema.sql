CREATE TABLE "user"
(
    "id"         UUID         PRIMARY KEY,
    "email"      VARCHAR(255) NOT NULL,
    "nickname"   VARCHAR(255) NOT NULL,
    "password"   VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMPTZ  NOT NULL,
    "deleted"    BOOLEAN      NULL
);

CREATE TABLE "article"
(
    "id"           UUID          PRIMARY KEY,
    "source"       VARCHAR(255)  NOT NULL,
    "source_url"   VARCHAR(2048) NOT NULL,
    "title"        VARCHAR(255)  NOT NULL,
    "publish_date" TIMESTAMPTZ   NOT NULL,
    "summary"      VARCHAR(255)  NOT NULL,
    "deleted"      BOOLEAN       NULL
);

CREATE TABLE "comment"
(
    "id"         UUID         PRIMARY KEY,
    "user_id"    UUID         NOT NULL,
    "article_id" UUID         NOT NULL,
    "content"    VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMPTZ  NOT NULL,
    "deleted"    BOOLEAN      NULL
);

CREATE TABLE "notification"
(
    "id"            UUID         PRIMARY KEY,
    "user_id"       UUID         NOT NULL,
    "resource_id"   UUID         NOT NULL,
    "resource_type" VARCHAR(50)  NOT NULL,
    "content"       VARCHAR(255) NOT NULL,
    "created_at"    TIMESTAMPTZ  NOT NULL,
    "updated_at"    TIMESTAMPTZ  NULL,
    "confirmed"     BOOLEAN      NOT NULL
);

CREATE TABLE "interest"
(
    "id"       UUID        PRIMARY KEY,
    "name"     VARCHAR(50) NOT NULL,
    "keywords" TEXT[]      NULL
);

CREATE TABLE "user_interest"
(
    "user_id"     UUID,
    "interest_id" UUID,
    "created_at"  TIMESTAMPTZ NULL
);

CREATE TABLE "article_views"
(
    "user_id"    UUID,
    "article_id" UUID,
    "created_at" TIMESTAMPTZ NOT NULL
);

CREATE TABLE "likes"
(
    "id"         UUID        PRIMARY KEY,
    "comment_id" UUID        NOT NULL,
    "user_id"    UUID        NOT NULL,
    "created_at" TIMESTAMPTZ NOT NULL
);

CREATE TABLE "articles_interest"
(
    "article_id"  UUID NOT NULL,
    "interest_id" UUID NOT NULL
);

ALTER TABLE "comment"
    ADD CONSTRAINT "FK_user_TO_comment_1"
        FOREIGN KEY ("user_id")
            REFERENCES "user" ("id")
            ON DELETE SET NULL;

ALTER TABLE "comment"
    ADD CONSTRAINT "FK_article_TO_comment_1"
        FOREIGN KEY ("article_id")
            REFERENCES "article" ("id")
            ON DELETE CASCADE;

ALTER TABLE "notification"
    ADD CONSTRAINT "FK_user_TO_notification_1"
        FOREIGN KEY ("user_id")
            REFERENCES "user" ("id")
            ON DELETE CASCADE;

ALTER TABLE "user_interest"
    ADD CONSTRAINT "FK_user_TO_user_interest_1"
        FOREIGN KEY ("user_id")
            REFERENCES "user" ("id")
            ON DELETE CASCADE;

ALTER TABLE "user_interest"
    ADD CONSTRAINT "FK_interest_TO_user_interest_1"
        FOREIGN KEY ("interest_id")
            REFERENCES "interest" ("id")
            ON DELETE CASCADE;

ALTER TABLE "article_views"
    ADD CONSTRAINT "FK_user_TO_article_views_1"
        FOREIGN KEY ("user_id")
            REFERENCES "user" ("id")
            ON DELETE CASCADE;

ALTER TABLE "article_views"
    ADD CONSTRAINT "FK_article_TO_article_views_1"
        FOREIGN KEY ("article_id")
            REFERENCES "article" ("id")
            ON DELETE CASCADE;

ALTER TABLE "likes"
    ADD CONSTRAINT "FK_comment_TO_likes_1"
        FOREIGN KEY ("comment_id")
            REFERENCES "comment" ("id")
            ON DELETE CASCADE;

ALTER TABLE "likes"
    ADD CONSTRAINT "FK_user_TO_likes_1"
        FOREIGN KEY ("user_id")
            REFERENCES "user" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_interest"
    ADD CONSTRAINT "FK_article_TO_articles_interest_1"
        FOREIGN KEY ("article_id")
            REFERENCES "article" ("id")
            ON DELETE CASCADE;

ALTER TABLE "articles_interest"
    ADD CONSTRAINT "FK_interest_TO_articles_interest_1"
        FOREIGN KEY ("interest_id")
            REFERENCES "interest" ("id")
            ON DELETE CASCADE;
