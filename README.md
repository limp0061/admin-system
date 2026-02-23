# 🛠️ ADMIN ERP SYSTEM

> **SpringBoot & Security 기반의 동적 권한 관리 시스템**
---

### 1. 프로젝트 소개

인사/조직을 관리하기 위한 관리자 ERP 시스템 입니다.  
정적인 권한 설정에서 벗어나서 동적으로 리소스 접근 정책을 변경할 수 있는 **유연한 시스템
구현을 목표**로 합니다.

### 2. 핵심 기술

**Backend**

* Java 17, Spring Boot 3.x, Spring Security
* JPA, Querydsl, JUnit5 (Integration Testing)
* Swagger, Spring RestDocs

**Frontend**

* Thymeleaf, TypeScript, Tailwind CSS
* CKEditor 5

**Database & Infra**

* MySQL, Redis (Pub/Sub)
* Docker, AWS S3 (File Storage)
* GCP (배포)

### 3. 주요 기능

* **동적 권한 제어**: 서버 재시작 없이 접근 제어 정책 실시간 반영
* **실시간 알림 시스템**: Redis Pub/Sub을 활용하여 실시간 공지사항 알림 시스템
* **부서 및 권한 체계**: 계층형 권한(Role Hierarchy) 및 조직 관리 로직

### 4. 구현 예정 기능

* CSRF 및 CORS 보안 설정 적용
* 대시보드 및 통계 기능 구현
* 로그 정리 및 데이터 로깅
* 사용자 개별 설정
* 다크 모드 (UI 전체적용 예정)
* 배치 스케줄러(파일 삭제 등)

### 5. 실행방법

#### 1. 사전 요구 사항

* **Docker Desktop**: 설치 및 실행 상태여야 합니다.
* **Java 17**: JDK 17 이상의 환경이 필요합니다.

#### 2. 로컬 환경 설정 및 실행 순서

1. **설정 파일 준비**
    * `application-secret.yml.sample` 파일의 이름을 `application-secret.yml`로 변경합니다.
2. **프로젝트 빌드**
    ```bash
    ./gradlew clean build
    ```
3. **인프라 실행**
    ```bash
    docker compose -f docker-compose-local.yml up -d
    ```
4. **애플리케이션 실행**
    * Spring Boot 메인 클래스 실행 또는 터미널에서 `./gradlew bootRun` 입력

### 6. 테스트 계정

| 이메일               | 비밀번호 | 권한          |
|-------------------|------|-------------|
| user1@kyj2579.com | 1234 | SUPER (최상위) |
