# 🌟 팀이름 : **MonewMonet**



## 👨‍💻 **팀원 소개**


<div align="center">

| 박유진 | 이요한 | 장태준 | 전성삼 | 허원재 |
|:---:|:---:|:---:|:---:|:---:|
| <img width="160" src="https://github.com/user-attachments/assets/fcad6410-bc99-4ca3-80b9-5b259947baf4" alt="yudility"> | <img width="160" src="" alt="arlegro"> | <img width="160" src="https://github.com/user-attachments/assets/94d07eb2-9788-40b2-872f-c675c8f99018" alt="janghoosa"> | <img width="160" src="https://github.com/user-attachments/assets/a856bd02-ab08-4347-83f9-6cfa01b663ea" alt="hodu31"> | <img width="160" src="" alt="oince"> |
| [Yudility](https://github.com/yudility) | [ARlegro](https://github.com/ARlegro) | [janghoosa](https://github.com/janghoosa) | [hodu31](https://github.com/hodu31) | [Oince](https://github.com/Oince) |

</div>

---

## 📰 **프로젝트 소개**

- 프로젝트명 : Monew (모뉴)
- 한줄 설명 : <br>개인 맞춤형 뉴스 스크랩과 소셜 기능을 제공하는 스마트 뉴스 플랫폼
- 상세 설명 : <br>Monew는 다양한 뉴스 API 및 RSS로부터 뉴스를 수집하고, 사용자 관심사 기반으로 선별하여 제공하는 맞춤형 뉴스 스크랩 서비스입니다.
사용자는 관심사에 따라 뉴스를 구독하고, 기사에 댓글을 남기거나 좋아요를 누르며 소통할 수 있습니다.
사용자 활동을 MongoDB에 저장하여 조회 성능을 최적화하며, 기사 데이터는 정기적으로 AWS S3에 백업·복구됩니다.

---

## 🛠 기술 스택

| 분야        | 기술 스택                                                    |
|-------------|-------------------------------------------------------------|
| **백엔드**   | Spring Boot, Spring Batch, Spring Data JPA, QueryDSL        |
| **데이터베이스** | PostgreSQL, MongoDB                                          |
| **인프라**    | AWS S3, AWS ECS, GitHub Actions, ECR, MongoDB Atlas, RDS, CodeDeploy |
| **테스트**    | JUnit 5, Mockito                                             |

---

## 👥 팀원별 구현 기능 상세

| 이름 | 담당 영역 | 구현 내용 |
|------|-----------|-----------|
| **박유진** | 관심사, 알림 | - 관심사 등록/수정/삭제/조회 로직 <br> - 키워드 기반 뉴스 필터링 <br> - 구독 기반 알림 생성 및 확인/삭제 기능 <br> - 커서 기반 페이지네이션  |
| **이요한** | 배치, 백업/복구, 알림 | - Spring Batch 기반 뉴스 기사 수집 및 알림 자동화  <br> - AWS S3를 통한 기사 백업 및 복구 프로세스 구현 <br>- 알림 자동 삭제 배치 및 Spring Actuator로 모니터링  |
| **장태준** | DB 관리, 활동 내역 | - PostgreSQL 및 테스트 DB 초기 설정 <br> - 사용자 활동 내역 설계 및 MongoDB 연동 <br> - 역정규화 모델 저장/조회 최적화  |
| **전성삼** | CI/CD, 인프라, 사용자관리 |- AWS ECS, CodeDeploy, S3 등 AWS 구성  <br> - GitHub Actions 기반 CI/CD 파이프라인 설계 <br> - Dockerfile, docker-compose.yml 구성 <br> - 사용자 등록/수정/삭제/로그인 로직 |
| **허원재** | 뉴스 기사, 댓글 | - 뉴스 API/RSS 연동 및 키워드 기반 필터링  <br> - 댓글 등록/수정/삭제, 좋아요 기능 구현  <br>- 기사 상세에 댓글 리스트 및 정렬 적용  |

---

## 🤝 협업 방식

저희 팀은 효율적인 협업을 위해 **정기적인 공유 시간**, **일정 관리 툴**, **이슈 기반 태스크 관리** 등을 체계적으로 운영하였습니다.


### 🕘 **Daily 협업 루틴**

- **오전 09:00 ~ 10:00**  
  → GitHub에서 서로의 PR을 리뷰하고, 코드 품질을 함께 개선합니다.
- **오전 10:00 ~ 10:20**  
  → 오늘 할 일을 각자 정리하여 `todo list`로 공유합니다.


### 🗓 **Notion 기반 일정 관리**

- 전체 일정은 **Notion Gantt Chart**로 시각화하여 관리하였습니다.
- 마감일, 현재 작업 단계, 진척 상태 등을 직관적으로 확인할 수 있도록 하였습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ed0ac1ed-f914-41d8-ab36-01d8ee67fa83" width="800"/>
</p>


### 📌 **GitHub Project를 통한 업무 추적**

- `Issue` 기반으로 업무를 정의하고, `Project Board`를 통해 진행 상황을 시각적으로 관리합니다.
- `ToDo`, `In Progress`, `Done` 칼럼으로 구성해, 실시간 협업을 강화했습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/385b38c5-4805-4790-b235-22bcbae4e4ee" width="800"/>
</p>


### 🧠 **기술 공유 및 이슈 정리**

- 프로젝트 도중 마주한 **기술적 이슈**, **트러블슈팅 과정**, **공유할 기술 스택**은 GitHub Issue로 문서화하여 기록했습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/6f95097a-0918-4edc-b4d4-6c07209482c5" width="800"/>
</p>


## **파일 구조**

```
```

---

## **구현 홈페이지**



---

## **프로젝트 회고록**

