# 📌 SFeed (스피드)

> **S**peed x **S**peak x News**feed**  
> 빠르게 전하는 소식, 함께 나누는 소통의 공간

<img width="406" height="406" alt="Image" src="https://github.com/user-attachments/assets/8f84390a-ecc1-4431-a7bc-77132a9310dd" />


## 📝 프로젝트 개요
- Spring Boot 기반의 **SNS 백엔드 서버**로,  
회원 가입부터 게시물 작성·조회·수정·삭제, 댓글 및 좋아요, 친구·팔로우 관리, 차단, 이메일 인증 등  
**SNS 핵심 기능**을 모두 포함한 프로젝트입니다.

---

## 🏗 아키텍처
- **Controller – Service – Repository – Entity – DTO**의 계층형 아키텍처를 적용하여  
책임을 명확히 분리하고 유지보수성을 높였습니다.

---
## 🔗 REST API 설계
- 프로젝트의 API는 RESTful 규칙을 준수하여, 리소스 중심의 경로 설계와 표준 HTTP 메서드(`GET`, `POST`, `PATCH`, `DELETE`) 매핑을 적용하였습니다.  
---

## 💾 데이터베이스 & 인증
- **데이터 매핑**: Spring Data JPA와 MySQL을 기반으로  
  엔티티(Entity)와 데이터베이스 테이블 간 매핑 자동화  
- **인증 방식**: HttpSession 기반 인증으로 로그인 상태를 유지 및 관리

---

## ⏳ 개발 기간
**2025.08.04 ~ 2025.08.15 (총 2주)**

---

## 👥 팀 구성
| 이름   | 역할   | 담당 기능 |
|--------|--------|-----------|
| 최용현 | 팀장   | 프로필 관리, 이메일 인증(SMTP) |
| 배연주 | 팀원   | 회원 탈퇴, 친구 차단 |
| 이수빈 | 팀원   | 게시물 CRUD(수정·삭제·피드조회), 게시물 차단 |
| 이연우 | 팀원   | 게시물 CRUD(생성·단건조회·전체조회), 팔로잉/팔로워 목록 조회, 좋아요·댓글, 기간별 조회 |
| 지아현 | 팀원   | 회원가입·로그인, 비밀번호 찾기, 아는 사람 기능 |


---

## 🛠 기술 스택
![java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![mysql](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

- **Language / Framework**: Java 17, Spring Boot, Spring Web, Spring Data JPA, Thymeleaf  
- **Database**: MySQL  
- **Authentication**: HttpSession  
- **Build Tool**: Gradle  
- **Etc**: Lombok, Validation (Jakarta)

---

## ✨ 주요 기능

### 👤 회원
- 회원가입, 로그인/로그아웃
- 이메일 인증 (발송 / 수락)
- 회원 정보 조회 / 수정
- 비밀번호 변경 / 찾기
- 회원 탈퇴 (**Soft Delete**)

### 📰 게시물
- 게시물 작성 / 수정 / 삭제
- 단건 조회 / 전체 조회 (페이징)
- 좋아요 / 좋아요 취소
- 게시물 차단
- 팔로워 전용 피드 조회
- 기간별 조회

### 💬 댓글
- 댓글 CRUD
- 좋아요 / 좋아요 취소

### 👥 친구 · 팔로우
- 친구 요청 / 수락 / 거절 / 삭제
- 친구 목록 조회
- 친구 차단 / 차단 해제
- 팔로잉 / 팔로워 목록 조회
- 상대방과의 팔로우 상태 조회 (단건)

---

## 📂 프로젝트 구조

```
newsfeed/
└── src/
    └── main/
        ├── java/
        │   └── com/example/newsfeed/
        │       ├── config/                                 
        │       ├── controller/                               # Controller 레이어: 요청 처리 및 Service 호출
        │       │   ├── auth/
        │       │   │   └── AuthController.java
        │       │   ├── comment/
        │       │   │   └── CommentController.java
        │       │   ├── follow/
        │       │   │   ├── FollowController.java
        │       │   │   ├── FollowerBlockController.java
        │       │   │   └── FollowReadController.java
        │       │   ├── like/
        │       │   │   └── LikeController.java
        │       │   ├── password/
        │       │   │   └── SessionPasswordResetController.java
        │       │   ├── post/
        │       │   │   ├── PostController.java
        │       │   │   ├── PostFeedController.java
        │       │   │   └── PostHideController.java
        │       │   └── user/
        │       │       └── UserController.java
        │       ├── dto/                                      # DTO 레이어: 요청/응답 데이터 전달 객체
        │       │   ├── auth/
        │       │   │   ├── AuthLoginRequestDto.java
        │       │   │   └── AuthRequestDto.java
        │       │   ├── comment/
        │       │   │   ├── CommentsPageResponseDto.java
        │       │   │   ├── CommentsRequestDto.java
        │       │   │   └── CommentsResponseDto.java
        │       │   ├── follow/
        │       │   │   ├── BlockRequestDto.java
        │       │   │   ├── BlockResponseDto.java
        │       │   │   ├── FollowListDto.java
        │       │   │   ├── FollowRequestDto.java
        │       │   │   ├── FollowResponseDto.java
        │       │   │   ├── FollowResponseMessageDto.java
        │       │   │   └── FollowStatusDto.java
        │       │   ├── like/
        │       │   │   └── LikeStatusResponseDto.java
        │       │   ├── password/
        │       │   │   ├── ChangePasswordRequestDto.java
        │       │   │   ├── PasswordResetForgotRequestDto.java
        │       │   │   ├── PasswordResetRequestDto.java
        │       │   │   └── PasswordResetVerifyRequestDto.java
        │       │   ├── post/
        │       │   │   ├── PostFeedItemDto.java
        │       │   │   ├── PostFeedResponseDto.java
        │       │   │   ├── PostHideResponseDto.java
        │       │   │   ├── PostPageResponseDto.java
        │       │   │   ├── PostRequestDto.java
        │       │   │   ├── PostResponseDto.java
        │       │   │   └── PostUpdateRequestDto.java
        │       │   └── user/
        │       │       ├── DeleteUsersRequest.java
        │       │       ├── UserProfileResponseDto.java
        │       │       ├── UserProfileUpdateRequestDto.java
        │       │       ├── UserSummaryDto.java
        │       │       └── UserSummaryPageResponseDto.java
        │       ├── entity/                                   # Entity 레이어: JPA 엔티티 클래스
        │       │   ├── comment/
        │       │   │   └── Comments.java
        │       │   ├── follow/
        │       │   │   ├── Follows.java
        │       │   │   └── FollowsId.java
        │       │   ├── like/
        │       │   │   ├── CommentLike.java
        │       │   │   └── PostLike.java
        │       │   ├── post/
        │       │   │   ├── PostHide.java
        │       │   │   ├── PostImages.java
        │       │   │   └── Posts.java
        │       │   └── user/
        │       │       ├── BlockedUser.java
        │       │       ├── Email.java
        │       │       ├── ProfileUpdateHistory.java
        │       │       └── Users.java
        │       ├── exception/                                # 예외 클래스: 일부 커스텀 예외 처리
        │       │   ├── AlreadyDeletedException.java
        │       │   ├── InvalidCredentialsException.java
        │       │   └── PasswordRequiredException.java
        │       ├── repository/                               # Repository 레이어: DB 접근
        │       │   ├── auth/
        │       │   │   └── AuthRepository.java
        │       │   ├── comment/
        │       │   │   └── CommentRepository.java
        │       │   ├── follow/
        │       │   │   ├── FollowerBlockRepository.java
        │       │   │   └── FollowsRepository.java
        │       │   ├── like/
        │       │   │   ├── CommentLikeRepository.java
        │       │   │   └── PostLikeRepository.java
        │       │   ├── post/
        │       │   │   ├── PostImageRepository.java
        │       │   │   └── PostRepository.java
        │       │   └── user/
        │       │       ├── EmailRepository.java
        │       │       ├── ProfileUpdateHistoryRepository.java
        │       │       ├── UserRepository.java
        │       │       └── UserSummary.java
        │       └── service/                                  # Service 레이어: 비즈니스 로직 처리
        │           ├── auth/
        │           │   └── AuthService.java
        │           ├── comment/
        │           │   └── CommentService.java
        │           ├── follow/
        │           │   ├── FollowerBlockService.java
        │           │   ├── FollowReadService.java
        │           │   └── FollowService.java
        │           ├── like/
        │           │   └── LikeService.java
        │           ├── post/
        │           │   ├── PostDeleteService.java
        │           │   ├── PostFeedService.java
        │           │   ├── PostHideService.java
        │           │   ├── PostService.java
        │           │   └── PostUpdateService.java
        │           └── user/
        │               ├── EmailService.java
        │               ├── MailService.java
        │               └── UserService.java
        │       └── NewsfeedApplication.java                  # 메인 실행 클래스
        └── resources/
            └── application.properties
```

---

## 🚀 실행 방법
```bash
# 1. 프로젝트 클론
git clone https://github.com/milk-abocado/newsfeed.git
cd newsfeed

# 2. 빌드 & 실행
./gradlew build
./gradlew bootRun
```

---

## 📌 API

| 기능 | 기본 URL | Method | 전체 URL |
|------|----------|--------|----------|
| 게시물 수정 | /posts | PATCH | /posts/{postId} |
| 게시물 작성 (로그인한 사용자만 가능) | /posts | POST | /posts |
| 게시물 삭제 (soft delete) | /posts | DELETE | /posts/{postId} |
| 게시물 단건 조회 | /posts | GET | /posts/{postId} |
| 게시물 전체 조회 (페이징 지원) | /posts | GET | /posts |
| 회원 가입 | /auth | POST | /auth/signup |
| 이메일 인증 발송 | /auth | POST | /auth/send |
| 이메일 인증 수락 | /auth | GET | /auth/verify |
| 로그인 | /auth | POST | /auth/login |
| 로그아웃 | /auth | POST | /auth/logout |
| 친구 요청 | /followers | POST | /followers/{userId}/following |
| 친구 수락 | /followers | PATCH | /followers/{userId}/accept |
| 친구 거절 | /followers | PATCH | /followers/{userId}/reject |
| 친구 삭제 | /followers | DELETE | /followers/{userId} |
| 친구 리스트 조회 | /followers | GET | /followers/{userId}/follows |
| 프로필 수정 | /users | PATCH | /users/{userId} |
| 본인 비밀번호 수정 | /users | PATCH | /users/{userId}/change |
| 회원 탈퇴 | /users | DELETE | /users/{userId}/delete |
| 회원 정보 조회 | /users | GET | /users/{userId} |
| 댓글 작성 | /comments | POST | /posts/{postId}/comments |
| 댓글 조회 | /comments | GET | /posts/{postId}/comments |
| 댓글 수정 | /comments | PATCH | /comments/{commentId} |
| 댓글 삭제 | /comments | DELETE | /comments/{commentId} |
| 게시물 좋아요 | /likes | POST | /posts/{postId}/likes |
| 게시물 좋아요 취소 | /likes | DELETE | /posts/{postId}/likes |
| 댓글 좋아요 | /likes | POST | /comments/{commentId}/likes |
| 댓글 좋아요 취소 | /likes | DELETE | /comments/{commentId}/likes |
| 게시물 차단 | /posts | POST | /posts/{postId}/hide |
| 친구 차단 | /followers | POST | /block |
| 친구 차단 해제 | /followers | POST | /unblock |
| 차단한 사용자 목록 조회 | /followers | GET | /blocked |
| 특정 사용자 차단 여부 확인 | /followers | GET | /blocked/{targetUserId} |
| 팔로잉 목록 조회 | /followers | GET | /follows/{userId}/following |
| 팔로워 목록 조회 | /followers | GET | /follows/{userId}/followers |
| 상대와의 팔로우 상태 조회 (단건) | /followers | GET | /follows/status/{targetId} |
| 인증코드 발송 | /auth | POST | /auth/password/session/forget |
| 인증코드 검증 | /auth | POST | /auth/password/session/verify |
| 비밀번호 재설정 | /auth | POST | /auth/password/session/reset |

---

## 🗂 ERD
<img width="1856" height="1280" alt="Image" src="https://github.com/user-attachments/assets/cb705d3e-5e97-4208-877e-66ccdd901b8e" />

---

## 🎨 와이어프레임
- **[Figma](https://www.figma.com/design/slfS97YvREXePhMzGkCR7j/%EB%89%B4%EC%8A%A4%ED%94%BC%EB%93%9C?node-id=0-1&m=dev&t=xdSXdwKtkOLnQVvn-1)**

---

## 📏 Team Code Convention

### 브랜치 전략
- **main**: 배포용 브랜치
- **dev**: 통합 개발 브랜치
- **feature/{팀원 이니셜}**: 개인 개발 브랜치

### Commit Convention
- ✨ feat: 새로운 기능 추가
- 🎉 add: 신규 파일 생성 / 초기 세팅
- 🐛 fix: 버그 수정
- ♻️ refactor: 코드 리팩토링
- 🚚 move: 파일 이동/정리
- 🔥 delete: 기능/파일 삭제
- ✅ test: 테스트 코드 작성
- 🙈 gitfix: .gitignore 수정
- 🔨 script: build.gradle, docker compose 변경
- 📝 chore: 주석/변수명/클래스명 수정
- ⚡️ improve: 기능 개선
- 🔖 merge: 구현 기능 병합

---
