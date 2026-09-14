# ecommerce-api
JPA 활용하여 이커머스 API 만들어보기

## 기술 스택
- BackEnd
    - Java : JDK 17
        - 채택 이유
            - 아직 1.8을 쓰는 프로젝트도 많지만 17 오버 JDK를 사용하는 프로젝트도 많기에 이를 습득하기 위해 사용을 채택했다.
            - Spring boot 2 버전대는 17까지 지원하지만 3버전대 이상에서는 최소 JDK 스펙이 17 버전이기에 채택했다.
    - Spring boot : 3.1.12 (GA) 최종 릴리스 버전
        - 3 버전대 채택 이유
            - 최소 JDK 17버전 이상 지원
            - JDK 17과 호환성이 높아 17버전의 모든 기술 지원, springframework 6 버전대 기반의 최신 동향 기술
            - 2 버전대의 Security 설정, javax -> jakarta 등 3버전에서 변경된 사항을 <br> 이번 토이 프로젝트를 통해 익숙해지기 위해서 채택을 했다.
            - 3.1.x 버전에대 GA된 최종 버전을 채택, 여러 프로젝트에서 사용되고 있는 안정성이 검증된 버전을 사용하기위해 채택했다.
    - 빌드 관리 도구: Gradle
        - 채택 이유
            - xml 보다 구성에 있어 간결하고 유연한 구성 / 설정에 따라 xml 방식보다 빌드 속도가 높다는 이점
    - DB: PostgreSQL 16 (Docker) — admin-api/user-api가 공유. 운영 방식은 아직 검토 중.
    - ORM/쿼리: JPA(Hibernate) + QueryDSL 5.0.0 (동적 쿼리)
    - 인증: Spring Security + JWT(jjwt 0.12.3)
    - 테스트: JUnit5 + Testcontainers (dev DB와 분리된 격리 컨테이너로 통합 테스트 실행)
    - API 문서화: springdoc-openapi(Swagger)

## 아키텍처
Gradle 멀티모듈: `core` / `domain` / `infra` / `admin-api` / `user-api`

- `admin-api`(8081) / `user-api`(8080): 독립 배포되는 Spring Boot 앱
- `domain`: entity/service/dto/Repository 포트(순수 인터페이스)
- `infra`: Repository 어댑터(QueryDSL), 파일 저장 어댑터
- `core`: 액터를 모르는 순수 공용 라이브러리(JWT 검증 등)

`domain`은 `infra`를 컴파일 타임에 모른다(Repository 포트/어댑터 분리). 자세한 배경은
[`.ai/reviews/feature/2026-08-19-multi-module-admin-user-split.md`](.ai/reviews/feature/2026-08-19-multi-module-admin-user-split.md),
[`.ai/reviews/feature/2026-08-21-repository-port-adapter-refactor.md`](.ai/reviews/feature/2026-08-21-repository-port-adapter-refactor.md) 참고.

## 로컬 실행
1. `docker-compose up -d` (PostgreSQL 기동)
2. `./gradlew :admin-api:bootRun` (8081) / `./gradlew :user-api:bootRun` (8080)
3. Swagger: `http://localhost:8081/swagger-ui/index.html` (admin), 8080 (user)
4. 테스트: `./gradlew test` — Testcontainers로 격리 실행, dev DB 미사용

## 주요 기능
회원/관리자, 배송지, 상품, 상품 옵션, 찜, 장바구니, 주문, 파일 업로드, 코드 관리(CMS). 각 기능의 상세 요구사항은 [`docs/prd/00-index.md`](docs/prd/00-index.md) 참고.

## 프로젝트 문서
- 제품 요구사항: [`docs/prd/00-index.md`](docs/prd/00-index.md)
- 아키텍처/마이그레이션 기록: [`.ai/reviews/feature/`](.ai/reviews/feature/)
- 코딩 규칙: [`.claude/rules/`](.claude/rules/)
