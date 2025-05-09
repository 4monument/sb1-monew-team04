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
VALUES (gen_random_uuid(), 'NAVER', 'https://site.com/ai', '인공지능의 미래', 'AI가 바꾸는 세상에 대한 이야기',
        '2025-05-01T00:00:00Z', now()),
       (gen_random_uuid(), 'CHOSUN', 'https://site.com/health', '건강하게 사는 법', '건강을 유지하는 실용적인 팁',
        '2025-05-02T00:00:00Z', now());

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
INTO "likes" ("id", "comment_id", "user_id", "created_at")
SELECT gen_random_uuid(), c1.comment_id, u2.user_id, now()
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
     '최신 양자 컴퓨팅 기술의 발전과 산업 적용 사례', '2025-05-03T00:00:00Z', now()),
    (gen_random_uuid(), 'YONHAP', 'https://techcrunch.com/blockchain', '블록체인이 바꾸는 금융의 미래',
     '블록체인 기술의 금융권 적용과 미래 전망', '2025-05-04T00:00:00Z', now()),
    (gen_random_uuid(), 'YONHAP', 'https://mittr.com/robotics', '로봇 공학의 최신 동향',
     '자율주행 로봇과 산업용 로봇의 최신 기술 동향', '2025-05-05T00:00:00Z', now()),

    -- 건강 관련 기사
    (gen_random_uuid(), 'YONHAP', 'https://healthtoday.com/nutrition', '영양소의 균형과 장수',
     '올바른 영양 섭취로 건강한 삶을 유지하는 방법', '2025-05-06T00:00:00Z', now()),
    (gen_random_uuid(), 'YONHAP', 'https://medjournal.com/mental', '정신 건강의 중요성',
     '현대 사회에서 정신 건강을 유지하는 실용적 방법', '2025-05-07T00:00:00Z', now());

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

INSERT INTO "users" ("id", "email", "nickname", "password")
VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'jiyoon@example.com', '지윤', 'hashed_pw3'),
       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'hyunwoo@example.com', '현우', 'hashed_pw4');

WITH u3 AS (SELECT id AS user_id FROM users WHERE email = 'jiyoon@example.com'),
     u4 AS (SELECT id AS user_id FROM users WHERE email = 'hyunwoo@example.com'),
     i1 AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
     i2 AS (SELECT id AS interest_id FROM interests WHERE name = '건강')

INSERT
INTO "users_interests" ("id", "user_id", "interest_id", "created_at")
SELECT gen_random_uuid(), u3.user_id, i1.interest_id, now()
FROM u3,
     i1
UNION
SELECT gen_random_uuid(), u4.user_id, i2.interest_id, now()
FROM u4,
     i2;

INSERT INTO "articles" ("id", "source", "source_url", "title", "summary", "publish_date",
                        "created_at")
VALUES (gen_random_uuid(), 'NAVER', 'https://site.com/future-tech', '미래 기술 트렌드', '다가오는 10년을 이끌 기술들',
        now(), now()),
       (gen_random_uuid(), 'NAVER', 'https://site.com/fitness-life', '운동이 삶에 미치는 영향',
        '규칙적인 운동이 신체와 정신에 끼치는 긍정적 영향', now(), now());

WITH a6 AS (SELECT id AS article_id FROM articles WHERE title = '미래 기술 트렌드'),
     a7 AS (SELECT id AS article_id FROM articles WHERE title = '운동이 삶에 미치는 영향'),
     i1 AS (SELECT id AS interest_id FROM interests WHERE name = '기술'),
     i2 AS (SELECT id AS interest_id FROM interests WHERE name = '건강')

INSERT
INTO "articles_interests" ("id", "article_id", "interest_id", "created_at")
SELECT gen_random_uuid(), a6.article_id, i1.interest_id, now()
FROM a6,
     i1
UNION
SELECT gen_random_uuid(), a7.article_id, i2.interest_id, now()
FROM a7,
     i2;

INSERT INTO "users" ("id", "email", "nickname", "password")
VALUES ('00000000-0000-0000-0000-000000000001', 'user01@example.com', '유저01', 'hashed_pw01'),
       ('00000000-0000-0000-0000-000000000002', 'user02@example.com', '유저02', 'hashed_pw02'),
       ('00000000-0000-0000-0000-000000000003', 'user03@example.com', '유저03', 'hashed_pw03'),
       ('00000000-0000-0000-0000-000000000004', 'user04@example.com', '유저04', 'hashed_pw04'),
       ('00000000-0000-0000-0000-000000000005', 'user05@example.com', '유저05', 'hashed_pw05'),
       ('00000000-0000-0000-0000-000000000006', 'user06@example.com', '유저06', 'hashed_pw06'),
       ('00000000-0000-0000-0000-000000000007', 'user07@example.com', '유저07', 'hashed_pw07'),
       ('00000000-0000-0000-0000-000000000008', 'user08@example.com', '유저08', 'hashed_pw08'),
       ('00000000-0000-0000-0000-000000000009', 'user09@example.com', '유저09', 'hashed_pw09'),
       ('00000000-0000-0000-0000-000000000010', 'user10@example.com', '유저10', 'hashed_pw10'),
       ('00000000-0000-0000-0000-000000000011', 'user11@example.com', '유저11', 'hashed_pw11'),
       ('00000000-0000-0000-0000-000000000012', 'user12@example.com', '유저12', 'hashed_pw12'),
       ('00000000-0000-0000-0000-000000000013', 'user13@example.com', '유저13', 'hashed_pw13'),
       ('00000000-0000-0000-0000-000000000014', 'user14@example.com', '유저14', 'hashed_pw14'),
       ('00000000-0000-0000-0000-000000000015', 'user15@example.com', '유저15', 'hashed_pw15'),
       ('00000000-0000-0000-0000-000000000016', 'user16@example.com', '유저16', 'hashed_pw16'),
       ('00000000-0000-0000-0000-000000000017', 'user17@example.com', '유저17', 'hashed_pw17'),
       ('00000000-0000-0000-0000-000000000018', 'user18@example.com', '유저18', 'hashed_pw18'),
       ('00000000-0000-0000-0000-000000000019', 'user19@example.com', '유저19', 'hashed_pw19'),
       ('00000000-0000-0000-0000-000000000020', 'user20@example.com', '유저20', 'hashed_pw20'),
       ('00000000-0000-0000-0000-000000000021', 'user21@example.com', '유저21', 'hashed_pw21'),
       ('00000000-0000-0000-0000-000000000022', 'user22@example.com', '유저22', 'hashed_pw22'),
       ('00000000-0000-0000-0000-000000000023', 'user23@example.com', '유저23', 'hashed_pw23'),
       ('00000000-0000-0000-0000-000000000024', 'user24@example.com', '유저24', 'hashed_pw24'),
       ('00000000-0000-0000-0000-000000000025', 'user25@example.com', '유저25', 'hashed_pw25'),
       ('00000000-0000-0000-0000-000000000026', 'user26@example.com', '유저26', 'hashed_pw26'),
       ('00000000-0000-0000-0000-000000000027', 'user27@example.com', '유저27', 'hashed_pw27'),
       ('00000000-0000-0000-0000-000000000028', 'user28@example.com', '유저28', 'hashed_pw28'),
       ('00000000-0000-0000-0000-000000000029', 'user29@example.com', '유저29', 'hashed_pw29'),
       ('00000000-0000-0000-0000-000000000030', 'user30@example.com', '유저30', 'hashed_pw30'),
       ('00000000-0000-0000-0000-000000000031', 'user31@example.com', '유저31', 'hashed_pw31'),
       ('00000000-0000-0000-0000-000000000032', 'user32@example.com', '유저32', 'hashed_pw32'),
       ('00000000-0000-0000-0000-000000000033', 'user33@example.com', '유저33', 'hashed_pw33'),
       ('00000000-0000-0000-0000-000000000034', 'user34@example.com', '유저34', 'hashed_pw34'),
       ('00000000-0000-0000-0000-000000000035', 'user35@example.com', '유저35', 'hashed_pw35'),
       ('00000000-0000-0000-0000-000000000036', 'user36@example.com', '유저36', 'hashed_pw36'),
       ('00000000-0000-0000-0000-000000000037', 'user37@example.com', '유저37', 'hashed_pw37'),
       ('00000000-0000-0000-0000-000000000038', 'user38@example.com', '유저38', 'hashed_pw38'),
       ('00000000-0000-0000-0000-000000000039', 'user39@example.com', '유저39', 'hashed_pw39'),
       ('00000000-0000-0000-0000-000000000040', 'user40@example.com', '유저40', 'hashed_pw40'),
       ('00000000-0000-0000-0000-000000000041', 'user41@example.com', '유저41', 'hashed_pw41'),
       ('00000000-0000-0000-0000-000000000042', 'user42@example.com', '유저42', 'hashed_pw42'),
       ('00000000-0000-0000-0000-000000000043', 'user43@example.com', '유저43', 'hashed_pw43'),
       ('00000000-0000-0000-0000-000000000044', 'user44@example.com', '유저44', 'hashed_pw44'),
       ('00000000-0000-0000-0000-000000000045', 'user45@example.com', '유저45', 'hashed_pw45'),
       ('00000000-0000-0000-0000-000000000046', 'user46@example.com', '유저46', 'hashed_pw46'),
       ('00000000-0000-0000-0000-000000000047', 'user47@example.com', '유저47', 'hashed_pw47'),
       ('00000000-0000-0000-0000-000000000048', 'user48@example.com', '유저48', 'hashed_pw48'),
       ('00000000-0000-0000-0000-000000000049', 'user49@example.com', '유저49', 'hashed_pw49'),
       ('00000000-0000-0000-0000-000000000050', 'user50@example.com', '유저50', 'hashed_pw50');

-- INTERESTS (관심사) 추가 데이터
INSERT INTO "interests" ("id", "name", "keywords", "created_at")
VALUES
-- 1-10
(gen_random_uuid(), '요리', '[
  "요리",
  "레시피",
  "베이킹",
  "맛집",
  "음식"
]'::jsonb, now()),
(gen_random_uuid(), '여행', '[
  "여행",
  "관광",
  "휴가",
  "배낭여행",
  "해외"
]'::jsonb, now()),
(gen_random_uuid(), '음악', '[
  "음악",
  "공연",
  "콘서트",
  "악기",
  "가수"
]'::jsonb, now()),
(gen_random_uuid(), '영화', '[
  "영화",
  "시네마",
  "배우",
  "감독",
  "스트리밍"
]'::jsonb, now()),
(gen_random_uuid(), '독서', '[
  "독서",
  "책",
  "문학",
  "작가",
  "소설"
]'::jsonb, now()),
(gen_random_uuid(), '패션', '[
  "패션",
  "스타일",
  "의류",
  "트렌드",
  "쇼핑"
]'::jsonb, now()),
(gen_random_uuid(), '스포츠', '[
  "스포츠",
  "경기",
  "팀",
  "선수",
  "운동경기"
]'::jsonb, now()),
(gen_random_uuid(), '예술', '[
  "예술",
  "그림",
  "조각",
  "전시회",
  "미술관"
]'::jsonb, now()),
(gen_random_uuid(), '게임', '[
  "게임",
  "비디오게임",
  "콘솔",
  "e스포츠",
  "온라인게임"
]'::jsonb, now()),
(gen_random_uuid(), '자기계발', '[
  "자기계발",
  "성장",
  "목표",
  "성공",
  "자기관리"
]'::jsonb, now()),

-- 11-20
(gen_random_uuid(), '환경', '[
  "환경",
  "생태계",
  "지속가능성",
  "재활용",
  "친환경"
]'::jsonb, now()),
(gen_random_uuid(), '사진', '[
  "사진",
  "촬영",
  "카메라",
  "포토그래피",
  "편집"
]'::jsonb, now()),
(gen_random_uuid(), '댄스', '[
  "댄스",
  "춤",
  "안무",
  "발레",
  "힙합"
]'::jsonb, now()),
(gen_random_uuid(), '정원', '[
  "정원",
  "가드닝",
  "식물",
  "원예",
  "조경"
]'::jsonb, now()),
(gen_random_uuid(), '반려동물', '[
  "반려동물",
  "애완동물",
  "강아지",
  "고양이",
  "펫케어"
]'::jsonb, now()),
(gen_random_uuid(), '역사', '[
  "역사",
  "문화유산",
  "유적지",
  "고대사",
  "역사인물"
]'::jsonb, now()),
(gen_random_uuid(), '과학', '[
  "과학",
  "연구",
  "실험",
  "발견",
  "천문학"
]'::jsonb, now()),
(gen_random_uuid(), '경제', '[
  "경제",
  "투자",
  "금융",
  "주식",
  "시장"
]'::jsonb, now()),
(gen_random_uuid(), '교육', '[
  "교육",
  "학습",
  "강의",
  "학교",
  "지식"
]'::jsonb, now()),
(gen_random_uuid(), '명상', '[
  "명상",
  "마인드풀니스",
  "요가",
  "정신건강",
  "힐링"
]'::jsonb, now()),

-- 21-30
(gen_random_uuid(), '인테리어', '[
  "인테리어",
  "홈데코",
  "가구",
  "디자인",
  "공간활용"
]'::jsonb, now()),
(gen_random_uuid(), '사회봉사', '[
  "사회봉사",
  "자원봉사",
  "기부",
  "사회공헌",
  "나눔"
]'::jsonb, now()),
(gen_random_uuid(), '언어', '[
  "언어",
  "외국어",
  "번역",
  "영어",
  "다국어"
]'::jsonb, now()),
(gen_random_uuid(), '커피', '[
  "커피",
  "카페",
  "바리스타",
  "로스팅",
  "브루잉"
]'::jsonb, now()),
(gen_random_uuid(), '와인', '[
  "와인",
  "양조",
  "테이스팅",
  "포도주",
  "소믈리에"
]'::jsonb, now()),
(gen_random_uuid(), '캠핑', '[
  "캠핑",
  "아웃도어",
  "텐트",
  "백패킹",
  "자연"
]'::jsonb, now()),
(gen_random_uuid(), '심리학', '[
  "심리학",
  "심리",
  "상담",
  "치료",
  "행동"
]'::jsonb, now()),
(gen_random_uuid(), '철학', '[
  "철학",
  "사상",
  "윤리",
  "논리",
  "철학자"
]'::jsonb, now()),
(gen_random_uuid(), '블로깅', '[
  "블로깅",
  "컨텐츠제작",
  "SNS",
  "인플루언서",
  "글쓰기"
]'::jsonb, now()),
(gen_random_uuid(), 'DIY', '[
  "DIY",
  "수공예",
  "핸드메이드",
  "창작",
  "리폼"
]'::jsonb, now()),

-- 31-40
(gen_random_uuid(), '자동차', '[
  "자동차",
  "드라이빙",
  "자동차정비",
  "카레이싱",
  "오토바이"
]'::jsonb, now()),
(gen_random_uuid(), '패션디자인', '[
  "패션디자인",
  "의상디자인",
  "재봉",
  "패턴",
  "원단"
]'::jsonb, now()),
(gen_random_uuid(), '비즈니스', '[
  "비즈니스",
  "창업",
  "스타트업",
  "경영",
  "리더십"
]'::jsonb, now()),
(gen_random_uuid(), '드론', '[
  "드론",
  "항공촬영",
  "무인기",
  "RC",
  "비행"
]'::jsonb, now()),
(gen_random_uuid(), '우주', '[
  "우주",
  "천문학",
  "별자리",
  "은하",
  "행성"
]'::jsonb, now()),
(gen_random_uuid(), '미식', '[
  "미식",
  "맛집탐방",
  "푸드투어",
  "다이닝",
  "음식평론"
]'::jsonb, now()),
(gen_random_uuid(), '크립토', '[
  "크립토",
  "암호화폐",
  "블록체인",
  "비트코인",
  "NFT"
]'::jsonb, now()),
(gen_random_uuid(), '뷰티', '[
  "뷰티",
  "화장품",
  "메이크업",
  "스킨케어",
  "미용"
]'::jsonb, now()),
(gen_random_uuid(), '디자인', '[
  "디자인",
  "그래픽디자인",
  "일러스트",
  "타이포그래피",
  "시각디자인"
]'::jsonb, now()),
(gen_random_uuid(), '보드게임', '[
  "보드게임",
  "카드게임",
  "테이블게임",
  "전략게임",
  "퍼즐"
]'::jsonb, now()),

-- 41-50
(gen_random_uuid(), '마라톤', '[
  "마라톤",
  "러닝",
  "트레일러닝",
  "조깅",
  "달리기"
]'::jsonb, now()),
(gen_random_uuid(), '해양활동', '[
  "해양활동",
  "서핑",
  "다이빙",
  "요트",
  "수영"
]'::jsonb, now()),
(gen_random_uuid(), '수집', '[
  "수집",
  "컬렉션",
  "수집품",
  "앤티크",
  "빈티지"
]'::jsonb, now()),
(gen_random_uuid(), '공예', '[
  "공예",
  "도자기",
  "도예",
  "목공",
  "유리공예"
]'::jsonb, now()),
(gen_random_uuid(), '문화', '[
  "문화",
  "전통",
  "풍습",
  "문화유산",
  "민속"
]'::jsonb, now()),
(gen_random_uuid(), '생태관광', '[
  "생태관광",
  "에코투어",
  "자연보호",
  "생태계",
  "국립공원"
]'::jsonb, now()),
(gen_random_uuid(), '천문학', '[
  "천문학",
  "별관측",
  "망원경",
  "천체",
  "우주탐사"
]'::jsonb, now()),
(gen_random_uuid(), '농업', '[
  "농업",
  "텃밭",
  "유기농",
  "작물재배",
  "도시농업"
]'::jsonb, now()),
(gen_random_uuid(), '로봇공학', '[
  "로봇공학",
  "로봇",
  "자동화",
  "메카트로닉스",
  "인공지능"
]'::jsonb, now()),
(gen_random_uuid(), '영적성장', '[
  "영적성장",
  "명상",
  "영성",
  "내면탐구",
  "자아성찰"
]'::jsonb, now());