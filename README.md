# 📌 SFeed (스피드)
<img width="406" height="406" alt="Image" src="https://github.com/user-attachments/assets/8f84390a-ecc1-4431-a7bc-77132a9310dd" />
---

## 📝 프로젝트 개요
- Spring Boot 기반의 **SNS 백엔드 서버**로,  
회원 가입부터 게시물 작성·조회·수정·삭제, 댓글 및 좋아요, 친구·팔로우 관리, 차단, 이메일 인증 등  
**SNS 핵심 기능**을 모두 포함한 프로젝트입니다.

---

## 🏗 아키텍처
- **Controller – Service – Repository – Entity – DTO**의 계층형 아키텍처를 적용하여  
책임을 명확히 분리하고 유지보수성을 높였습니다.

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

```bash
newsfeed/
└── src/
    └── main/
        ├── java/
        │   └── com/example/newsfeed/
        │       ├── NewsfeedApplication.java
        │       ├── config/                       # 전역 설정 (CORS, 인터셉터 등)
        │       ├── controller/                   # API 계층
        │       │   ├── AuthController.java
        │       │   ├── CommentController.java
        │       │   ├── FollowController.java
        │       │   ├── FollowerBlockController.java
        │       │   ├── FollowReadController.java
        │       │   ├── LikeController.java
        │       │   ├── PostController.java
        │       │   ├── PostDeleteController.java
        │       │   ├── PostFeedController.java
        │       │   ├── PostHideController.java
        │       │   ├── PostUpdateController.java
        │       │   ├── SessionPasswordResetController.java
        │       │   └── UserController.java
        │       ├── dto/                          # 요청 / 응답 DTO
        │       │   ├── AuthLoginRequestDto.java
        │       │   ├── AuthRequestDto.java
        │       │   ├── BlockRequestDto.java
        │       │   ├── ChangePasswordRequestDto.java
        │       │   ├── CommentsPageResponseDto.java
        │       │   ├── CommentsRequestDto.java
        │       │   ├── CommentsResponseDto.java
        │       │   ├── DeleteUsersRequest.java
        │       │   ├── EmailRequestDto.java
        │       │   ├── FollowListDto.java
        │       │   ├── FollowRequestDto.java
        │       │   ├── FollowResponseDto.java
        │       │   ├── FollowResponseMessageDto.java
        │       │   ├── FollowStatusDto.java
        │       │   ├── LikeStatusResponseDto.java
        │       │   ├── PasswordResetForgotRequestDto.java
        │       │   ├── PasswordResetResetRequestDto.java
        │       │   ├── PasswordResetVerifyRequestDto.java
        │       │   ├── PostFeedItemDto.java
        │       │   ├── PostFeedResponseDto.java
        │       │   ├── PostHideResponseDto.java
        │       │   ├── PostPageResponseDto.java
        │       │   ├── PostRequestDto.java
        │       │   ├── PostResponseDto.java
        │       │   ├── PostUpdateRequestDto.java
        │       │   ├── UserProfileResponseDto.java
        │       │   ├── UserProfileUpdateRequestDto.java
        │       │   ├── UserSummaryDto.java
        │       │   └── UserSummaryPageResponseDto.java
        │       ├── entity/                       # JPA 엔티티
        │       │   ├── BlockedUser.java
        │       │   ├── CommentLike.java
        │       │   ├── Comments.java
        │       │   ├── Email.java
        │       │   ├── Follows.java
        │       │   ├── FollowsId.java
        │       │   ├── PostHide.java
        │       │   ├── PostImages.java
        │       │   ├── PostLike.java
        │       │   ├── Posts.java
        │       │   ├── ProfileUpdateHistory.java
        │       │   └── Users.java
        │       ├── exception/                    # 커스텀 예외
        │       │   ├── AlreadyDeletedException.java
        │       │   ├── InvalidCredentialsException.java
        │       │   └── PasswordRequiredException.java
        │       ├── repository/                   # 데이터 접근 계층
        │       │   ├── AuthRepository.java
        │       │   ├── CommentLikeRepository.java
        │       │   ├── CommentRepository.java
        │       │   ├── EmailRepository.java
        │       │   ├── FollowerBlockRepository.java
        │       │   ├── FollowsRepository.java
        │       │   ├── PostImageRepository.java
        │       │   ├── PostLikeRepository.java
        │       │   ├── PostRepository.java
        │       │   ├── ProfileUpdateHistoryRepository.java
        │       │   ├── UserRepository.java
        │       │   └── UserSummary.java
        │       └── service/                      # 비즈니스 로직
        │           ├── AuthService.java
        │           ├── CommentService.java
        │           ├── EmailService.java
        │           ├── FollowerBlockService.java
        │           ├── FollowReadService.java
        │           ├── FollowService.java
        │           ├── LikeService.java
        │           ├── MailService.java
        │           ├── PostDeleteService.java
        │           ├── PostFeedService.java
        │           ├── PostHideService.java
        │           ├── PostService.java
        │           ├── PostUpdateService.java
        │           └── UserService.java
        └── resources/
            ├── application.properties
            └── templates/                        # Thymeleaf 템플릿
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
(추가 예정)

---

## 🗂 ERD
<img width="1856" height="1280" alt="Image" src="https://github.com/user-attachments/assets/ec798339-93d7-41ff-8de1-06d0a004513c" />

---

## 🎨 와이어프레임
- **[Figma](https://www.figma.com/design/slfS97YvREXePhMzGkCR7j/%EB%89%B4%EC%8A%A4%ED%94%BC%EB%93%9C?node-id=0-1&m=dev&t=xdSXdwKtkOLnQVvn-1)**  

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

### 코드 스타일
1. DTO로 입출력 분리
2. Controller는 서비스 호출 및 DTO 변환만 담당
3. Service는 별도의 인터페이스 사용 X (필요 시 논의 후 적용)
4. 비즈니스 로직에서 중복 메서드는 추출하여 재사용

---

## 🐞 Troubleshooting
- 추후 추가

---
