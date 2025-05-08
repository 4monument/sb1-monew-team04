-- 확장 설치 (한 번만 실행)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE "users"
    ALTER COLUMN "created_at" SET DEFAULT now();
-- USERS (사용자)
INSERT INTO "users" ("id", "email", "nickname", "password")
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'minji@example.com', '민지', 'hashed_pw1'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'hoyeon@example.com', '호연', 'hashed_pw2');

-- INTERESTS (관심사)
INSERT INTO "interests" ("id", "name", "keywords", "created_at")
VALUES (gen_random_uuid(), '기술', '[
  "기술",
  "인공지능",
  "소프트웨어"
]'::jsonb, now()),
       (gen_random_uuid(), '건강', '[
         "운동",
         "웰빙",
         "의료"
       ]'::jsonb, now());

-- USERS_INTERESTS (사용자-관심사 연결)
WITH u1 AS (SELECT id AS user_id FROM users WHERE email = 'minji@example.com'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     i1 AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
     i2 AS (SELECT id AS interest_id FROM interests WHERE name = '건강')

INSERT
INTO "users_interests" ("id", "user_id", "interest_id", "created_at")
SELECT gen_random_uuid(), u1.user_id, i1.interest_id, now()
FROM u1,
     i1;
-- UNION
-- SELECT gen_random_uuid(), u2.user_id, i2.interest_id, now()
-- FROM u2,
--      i2;

-- ARTICLES (기사)
INSERT INTO "articles" ("id", "source", "source_url", "title", "summary", "publish_date",
                        "created_at")
VALUES (gen_random_uuid(), 'NAVER', 'https://site.com/ai', '인공지능의 미래', 'AI가 바꾸는 세상에 대한 이야기', now(),
        now()),
       (gen_random_uuid(), 'CHOSUN', 'https://site.com/health', '건강하게 사는 법', '건강을 유지하는 실용적인 팁',
        now(), now());

-- ARTICLES_INTERESTS (기사-관심사 연결)
WITH a1 AS (SELECT id AS article_id FROM articles WHERE title = '인공지능의 미래'),
     a2 AS (SELECT id AS article_id FROM articles WHERE title = '건강하게 사는 법'),
     i1 AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
     i2 AS (SELECT id AS interest_id FROM interests WHERE name = '건강')

INSERT
INTO "articles_interests" ("id", "article_id", "interest_id", "created_at")
SELECT gen_random_uuid(), a1.article_id, i1.interest_id, now()
FROM a1,
     i1
UNION
SELECT gen_random_uuid(), a2.article_id, i2.interest_id, now()
FROM a2,
     i2;

-- COMMENTS (댓글)
WITH au1 AS (SELECT id FROM users WHERE email = 'minji@example.com'),
     aa1 AS (SELECT id FROM articles WHERE title = '인공지능의 미래'),
     au2 AS (SELECT id FROM users WHERE email = 'hoyeon@example.com'),
     aa2 AS (SELECT id FROM articles WHERE title = '건강하게 사는 법')

INSERT
INTO "comments" ("id", "user_id", "article_id", "content", "created_at")
SELECT gen_random_uuid(), au1.id, aa1.id, '정말 흥미로운 기사네요!', now()
FROM au1,
     aa1
UNION
SELECT gen_random_uuid(), au2.id, aa2.id, '도움이 많이 됐어요!', now()
FROM au2,
     aa2;

-- LIKES (좋아요)
WITH c1 AS (SELECT id AS comment_id FROM comments WHERE content = '정말 흥미로운 기사네요!'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com')

INSERT
INTO "likes" ("id", "comment_id", "user_id")
SELECT gen_random_uuid(), c1.comment_id, u2.user_id
FROM c1,
     u2;

-- ARTICLES_VIEWS (기사 열람 기록)
WITH u1 AS (SELECT id AS user_id FROM users WHERE email = 'minji@example.com'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     a1 AS (SELECT id AS article_id FROM articles WHERE title = '인공지능의 미래'),
     a2 AS (SELECT id AS article_id FROM articles WHERE title = '건강하게 사는 법')

INSERT
INTO "articles_views" ("id", "user_id", "article_id", "created_at")
SELECT gen_random_uuid(), u1.user_id, a1.article_id, now()
FROM u1,
     a1;
-- UNION
-- SELECT gen_random_uuid(), u2.user_id, a2.article_id, now()
-- FROM u2,
--      a2;

-- NOTIFICATIONS (알림)
WITH u1 AS (SELECT id AS user_id FROM users WHERE email = 'minji@example.com'),
     u2 AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     c1 AS (SELECT id AS resource_id FROM comments WHERE content = '정말 흥미로운 기사네요!'),
     c2 AS (SELECT id AS resource_id FROM comments WHERE content = '도움이 많이 됐어요!')

INSERT
INTO "notifications" ("id", "user_id", "resource_id", "resource_type", "content", "confirmed",
                      "created_at", "updated_at")
SELECT '123e4567-e89b-12d3-a456-426614174000'::uuid,
       u1.user_id,
       c1.resource_id,
       'COMMENT',
       '누군가 내 댓글에 좋아요를 눌렀어요.',
       false,
       now(),
       now()
FROM u1,
     c1
UNION
SELECT '987e6543-e21b-12d3-b456-426614174000'::uuid,
       u2.user_id,
       c2.resource_id,
       'COMMENT',
       '새로운 댓글이 달렸어요.',
       false,
       now(),
       now()
FROM u2,
     c2;

SELECT conname, pg_get_constraintdef(c.oid)
FROM pg_constraint c
WHERE conrelid = 'notifications'::regclass;


-- 추가 기사 데이터
INSERT INTO "articles" ("id", "source", "source_url", "title", "summary", "publish_date",
                        "created_at")
VALUES
    -- 기술 관련 기사
    (gen_random_uuid(), 'NAVER', 'https://zdnet.com/quantum', '양자 컴퓨팅의 혁신적 발전',
     '최신 양자 컴퓨팅 기술의 발전과 산업 적용 사례', now(), now()),
    (gen_random_uuid(), 'YONHAP', 'https://techcrunch.com/blockchain', '블록체인이 바꾸는 금융의 미래',
     '블록체인 기술의 금융권 적용과 미래 전망', now(), now()),
    (gen_random_uuid(), 'YONHAP', 'https://mittr.com/robotics', '로봇 공학의 최신 동향',
     '자율주행 로봇과 산업용 로봇의 최신 기술 동향', now(), now()),

    -- 건강 관련 기사
    (gen_random_uuid(), 'YONHAP', 'https://healthtoday.com/nutrition', '영양소의 균형과 장수',
     '올바른 영양 섭취로 건강한 삶을 유지하는 방법', now(), now()),
    (gen_random_uuid(), 'YONHAP', 'https://medjournal.com/mental', '정신 건강의 중요성',
     '현대 사회에서 정신 건강을 유지하는 실용적 방법', now(), now());

-- 새 기사와 관심사 연결
WITH a1 AS (SELECT id AS article_id FROM articles WHERE title = '양자 컴퓨팅의 혁신적 발전'),
     a2 AS (SELECT id AS article_id FROM articles WHERE title = '블록체인이 바꾸는 금융의 미래'),
     a3 AS (SELECT id AS article_id FROM articles WHERE title = '로봇 공학의 최신 동향'),
     a4 AS (SELECT id AS article_id FROM articles WHERE title = '영양소의 균형과 장수'),
     a5 AS (SELECT id AS article_id FROM articles WHERE title = '정신 건강의 중요성'),
     i_tech AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
     i_health AS (SELECT id AS interest_id FROM interests WHERE name = '건강')

INSERT
INTO "articles_interests" ("id", "article_id", "interest_id", "created_at")
-- 기술 관련 기사 연결
SELECT gen_random_uuid(), a1.article_id, i_tech.interest_id, now()
FROM a1,
     i_tech
UNION
SELECT gen_random_uuid(), a2.article_id, i_tech.interest_id, now()
FROM a2,
     i_tech
UNION
SELECT gen_random_uuid(), a3.article_id, i_tech.interest_id, now()
FROM a3,
     i_tech
UNION
-- 건강 관련 기사 연결
SELECT gen_random_uuid(), a4.article_id, i_health.interest_id, now()
FROM a4,
     i_health
UNION
SELECT gen_random_uuid(), a5.article_id, i_health.interest_id, now()
FROM a5,
     i_health;

-- 호연 사용자에게 기술 관심사 추가 구독
WITH u_hoyeon AS (SELECT id AS user_id FROM users WHERE email = 'hoyeon@example.com'),
     i_tech AS (SELECT id AS interest_id FROM interests WHERE name = '기술')

INSERT
INTO "users_interests" ("id", "user_id", "interest_id", "created_at")
SELECT gen_random_uuid(), u_hoyeon.user_id, i_tech.interest_id, now()
FROM u_hoyeon,
     i_tech;