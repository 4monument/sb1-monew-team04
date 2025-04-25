-- USERS (사용자)
INSERT INTO "users" ("id", "email", "nickname", "password")
VALUES (gen_random_uuid(), 'minji@example.com', '민지', 'hashed_pw1'),
       (gen_random_uuid(), 'hoyeon@example.com', '호연', 'hashed_pw2');

-- INTERESTS (관심사)
INSERT INTO "interests" ("id", "name", "keywords", "created_at")
VALUES (gen_random_uuid(), '기술', '[ "기술", "인공지능", "소프트웨어" ]'::jsonb, now()),
       (gen_random_uuid(), '건강', '[ "운동", "웰빙", "의료" ]'::jsonb, now());

-- USERS_INTERESTS (사용자-관심사 연결)
INSERT INTO "users_interests" ("user_id", "interest_id", "created_at")
SELECT u.id AS user_id, i.id AS interest_id, now()
FROM "users" u
         JOIN "interests" i ON i.name = '기술'
WHERE u.email = 'minji@example.com'
UNION ALL
SELECT u.id AS user_id, i.id AS interest_id, now()
FROM "users" u
         JOIN "interests" i ON i.name = '건강'
WHERE u.email = 'hoyeon@example.com';

-- ARTICLES (기사)
INSERT INTO "articles" ("id", "source", "source_url", "title", "summary", "publish_date")
VALUES
    (gen_random_uuid(), 'NAVER', 'https://site.com/ai', '인공지능의 미래', 'AI가 바꾸는 세상에 대한 이야기', now()),
    (gen_random_uuid(), 'CHOSUN', 'https://site.com/health', '건강하게 사는 법', '건강을 유지하는 실용적인 팁', now());

-- ARTICLES_INTERESTS (기사-관심사 연결)
WITH
    a1 AS (SELECT id AS article_id FROM articles WHERE title = '인공지능의 미래'),
    a2 AS (SELECT id AS article_id FROM articles WHERE title = '건강하게 사는 법'),
    i1 AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
    i2 AS (SELECT id AS interest_id FROM interests WHERE name = '건강')
INSERT INTO "articles_interests" ("article_id", "interest_id")
SELECT a1.article_id, i1.interest_id FROM a1, i1
UNION
SELECT a2.article_id, i2.interest_id FROM a2, i2;

-- COMMENTS (댓글)
WITH au1 AS (SELECT id FROM users WHERE email = 'minji@example.com'),
     aa1 AS (SELECT id FROM articles WHERE title = '인공지능의 미래'),
     au2 AS (SELECT id FROM users WHERE email = 'hoyeon@example.com'),
     aa2 AS (SELECT id FROM articles WHERE title = '건강하게 사는 법')
INSERT INTO "comments" ("id", "user_id", "article_id", "content", "created_at")
SELECT gen_random_uuid(), au1.id, aa1.id, '정말 흥미로운 기사네요!', now() FROM au1, aa1
UNION
SELECT gen_random_uuid(), au2.id, aa2.id, '도움이 많이 됐어요!', now() FROM au2, aa2;

-- LIKES (좋아요)
WITH c1 AS (SELECT id AS comment_id FROM comments WHERE content = '정말 흥미로운 기사네요!'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com')
INSERT INTO "likes" ("id", "comment_id", "user_id")
SELECT gen_random_uuid(), c1.comment_id, u2.user_id FROM c1, u2;

-- ARTICLES_VIEWS (기사 열람 기록)
WITH u1 AS (SELECT id AS user_id FROM users WHERE email = 'minji@example.com'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     a1 AS (SELECT id AS article_id FROM articles WHERE title = '인공지능의 미래'),
     a2 AS (SELECT id AS article_id FROM articles WHERE title = '건강하게 사는 법')
INSERT INTO "articles_views" ("user_id", "article_id", "created_at")
SELECT u1.user_id, a1.article_id, now() FROM u1, a1
UNION
SELECT u2.user_id, a2.article_id, now() FROM u2, a2;

-- NOTIFICATIONS (알림)
WITH u1 AS (SELECT id AS user_id FROM users WHERE email = 'minji@example.com'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     c1 AS (SELECT id AS resource_id FROM comments WHERE content = '정말 흥미로운 기사네요!'),
     c2 AS (SELECT id AS resource_id FROM comments WHERE content = '도움이 많이 됐어요!')
INSERT INTO "notifications" ("id", "user_id", "resource_id", "resource_type", "content", "confirmed", "created_at", "updated_at")
SELECT gen_random_uuid(), u1.user_id, c1.resource_id, 'COMMENT', '누군가 내 댓글에 좋아요를 눌렀어요.', false, now(), now() FROM u1, c1
UNION
SELECT gen_random_uuid(), u2.user_id, c2.resource_id, 'COMMENT', '새로운 댓글이 달렸어요.', false, now(), now() FROM u2, c2;
