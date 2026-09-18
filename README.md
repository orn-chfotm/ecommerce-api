# ecommerce-api
JPA 활용하여 이커머스 API 만들어보기

## 기술 스택
- BackEnd
    - Java : JDK 17 (LTS)
        - 채택 이유
            - Spring Boot 3 버전대의 최소 JDK 스펙이 17이라 채택했다.
    - Spring boot : 3.1.12 (GA)
        - 3 버전대 채택 이유
            - 최소 JDK 17버전 이상 지원
            - JDK 17과 호환성이 높아 17버전의 모든 기술 지원, springframework 6 버전대 기반의 최신 동향 기술
            - 2 버전대의 Security 설정, javax -> jakarta 등 3버전에서 변경된 사항을 <br> 이번 토이 프로젝트를 통해 익숙해지기 위해서 채택을 했다.
            - 3.1.x 버전대의 GA 최종 릴리스를 채택했다. **단, 3.1 라인은 현재 OSS 지원이 종료(EOL)돼 보안 패치를 받지 못한다 — 신규로 시작한다면 지원 중인 버전을 채택할 것.**
    - Lombok: 보일러플레이트(생성자/getter 등) 제거, 전 모듈에서 사용
    - AWS S3 (`spring-cloud-aws-starter-s3`): 파일 업로드 저장소, `infra` 모듈
    - 빌드 관리 도구: Gradle
        - 채택 이유
            - xml 보다 구성에 있어 간결하고 유연한 구성 / 설정에 따라 xml 방식보다 빌드 속도가 높다는 이점
    - DB: PostgreSQL 16 (Docker) — admin-api/user-api가 공유. 운영 방식은 아직 검토 중.
    - ORM/쿼리: JPA(Hibernate) + QueryDSL 5.0.0 (동적 쿼리)
    - 인증: Spring Security + JWT(jjwt 0.12.3)
    - 테스트: JUnit5 + Testcontainers — `admin-api`/`user-api`의 통합 테스트가 PostgreSQL 컨테이너를 dev DB와 분리해 격리 실행한다. `domain` 모듈 테스트는 Spring Context 없는 순수 단위 테스트다.
    - API 문서화: springdoc-openapi(Swagger)

## 아키텍처
Gradle 멀티모듈: `core` / `domain` / `infra` / `admin-api` / `user-api`

- `admin-api`(8081) / `user-api`(8080): 독립 배포되는 Spring Boot 앱
- `domain`: entity/service/dto/Repository 포트(순수 인터페이스)
- `infra`: Repository 어댑터(QueryDSL), 파일 저장 어댑터
- `core`: 액터를 모르는 순수 공용 라이브러리(JWT 검증 등)

`domain`은 `infra`를 컴파일 타임에 모른다(Repository 포트/어댑터 분리).

## 로컬 실행
1. `docker-compose up -d` (PostgreSQL 기동)
2. `./gradlew :admin-api:bootRun` (8081) / `./gradlew :user-api:bootRun` (8080)
3. Swagger: `http://localhost:8081/swagger-ui/index.html` (admin), 8080 (user)
4. 테스트: `./gradlew test` — `admin-api`/`user-api`는 Testcontainers로 격리 실행(dev DB 미사용), `domain`은 순수 단위 테스트

## 주요 기능
회원/관리자, 배송지, 상품, 상품 옵션, 찜, 장바구니, 주문, 파일 업로드, 코드 관리(CMS). 각 기능의 상세 요구사항은 [`docs/prd/00-index.md`](docs/prd/00-index.md) 참고.
