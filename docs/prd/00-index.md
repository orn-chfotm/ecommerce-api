# PRD Index

이 디렉토리는 현재 구현된 ecommerce-be 백엔드 기준의 제품 요구사항 문서를 둔다.

2026-08-19부로 이 프로젝트는 Gradle 멀티 모듈(`core`/`domain`/`admin-api`/`user-api`)로 전환되었고, 2026-08-21에 `domain`에서 `infra`가 5번째 모듈로 분리되었다. 아래 문서 구조 자체(요구사항)는 여전히 유효하지만, 각 기능의 Controller가 어느 앱(`admin-api`/`user-api`)에 있는지, Repository가 어느 모듈(포트=`domain`, 구현체=`infra`)에 있는지는 `.claude/rules/layer/01-package.md`와 `.ai/reviews/feature/`의 관련 문서를 참고한다.

## 문서 구조

- 공통 및 인증: `docs/prd/01-common-auth.md`
- 회원 및 관리자: `docs/prd/02-member-admin.md`
- 배송지: `docs/prd/03-address.md`
- 상품: `docs/prd/04-product.md`
- 상품 옵션 및 옵션 템플릿: `docs/prd/05-product-options.md`
- 찜: `docs/prd/06-wish.md`
- 장바구니: `docs/prd/07-cart.md`
- 주문: `docs/prd/08-order.md`
- 파일 업로드 및 재고 규칙: `docs/prd/09-file-stock.md`
- 구현상 주의점: `docs/prd/10-current-gaps.md`

## 아키텍처 — Gradle 멀티 모듈 (2026-08-21 기준 5개 모듈)

관리자(Admin) 서버와 사용자(User) 서버를 분리 배포하고 `domain`이 `infra`(DB/파일 구현)에 컴파일 타임으로 의존하지 않도록 `core`/`domain`/`infra`/`admin-api`/`user-api` 5개 Gradle 모듈로 구성되어 있다. `./gradlew build` 기준 5개 모듈 전체 빌드 및 테스트 통과 확인됨.

- 설계/마이그레이션 기록: `.ai/reviews/feature/2026-08-19-multi-module-admin-user-split.md`(admin/user 분리), `.ai/reviews/feature/2026-08-21-infra-module-split.md`(infra 분리), `.ai/reviews/feature/2026-08-21-repository-port-adapter-refactor.md`(Repository가 `JpaRepository`를 extends하지 않도록 재설계), `.ai/reviews/feature/2026-09-09-shared-postgres-db.md`(H2 → 공유 PostgreSQL 전환, Testcontainers 기반 테스트 DB)
- 구조 요약: `core`(공용 라이브러리) ← `domain`(엔티티/서비스/Repository 포트, 순수 인터페이스) ← `infra`(Repository 구현체/파일 저장 어댑터) ← `admin-api`/`user-api`(각각 독립 배포되는 Spring Boot 앱, 포트 8081/8080)
- security/JWT는 액터를 모르는 순수 JWT 검증 로직만 `core`에 공유하고, admin/user 로그인 스택은 각 앱 모듈로 분리되어 있다.
- Repository는 포트(순수 인터페이스, `domain`)/어댑터(`JpaRepository`+QueryDSL 구현체, `infra`)로 분리되어 있다 — domain은 `extends JpaRepository`를 쓰지 않는다. `.claude/rules/querydsl/01-custom-repo.md` 참고.
- 상세 패키지/모듈 규칙: `.claude/rules/layer/01-package.md`

## Claude Code 연동 근거

- Claude Code 공식 문서 기준, 프로젝트 공용 지침은 루트 `CLAUDE.md` 또는 `.claude/CLAUDE.md`에 둘 수 있다.
- `CLAUDE.md`는 `@path/to/file.md` 형식으로 추가 Markdown 파일을 import할 수 있다.
- import된 파일은 시작 컨텍스트에 함께 로드되므로, 전체 PRD를 매 세션 로드하지 않기 위해 루트 `CLAUDE.md`는 이 index만 import한다.
- 제품 요구사항 확인이 필요하면 관련 기능 문서만 직접 읽는다.
