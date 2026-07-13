# 공통 및 인증 PRD

## 문서 범위

현재 코드에 구현된 공통 응답, 예외, 인증/인가 동작만 정리한다.

근거 파일:

- `build.gradle`
- `src/main/resources/application.yml`
- `src/main/java/com/build/ecommerce/core/**`
- `src/main/java/com/build/ecommerce/domain/auth/**`
- 각 도메인 controller의 권한 애너테이션

## 제품 개요

이 프로젝트는 Spring Boot 기반 이커머스 백엔드 API이다. 사용자와 관리자를 분리하고, 상품 등록/조회, 상품 옵션 및 SKU 관리, 장바구니, 주문, 배송지, 찜 목록 기능을 제공한다.

## 기술 전제

- Java 17
- Spring Boot 3.1.12
- Gradle
- Spring Web, Validation, Security, Data JPA
- QueryDSL
- H2 in-memory DB 기본 profile
- JWT 인증
- springdoc-openapi
- 로컬 파일 저장소와 prod profile용 AWS S3 파일 저장소

## 사용자 역할

### 비회원

- 사용자 회원가입 가능
- 관리자 가입 가능
- 상품 목록/상세 조회 가능
- 옵션 템플릿 목록/상세 조회 가능
- JWT refresh 요청 가능

### 사용자

- 본인 회원 정보 조회 가능
- 배송지 등록/조회 가능
- 상품 찜 등록/조회/상세조회/삭제 가능
- 장바구니 등록/조회/수정/삭제/전체삭제 가능
- 주문 생성/목록조회/상세조회/취소 가능

### 관리자

- 관리자 정보 조회 가능
- 상품 등록/삭제 가능
- 상품 옵션 등록/조회/옵션 variant 재고 수정 가능
- 옵션 템플릿 등록/조회/수정/삭제 가능
- 배송지 조회/등록 API 접근 가능

## 공통 API 응답

모든 주요 API는 `SuccessResponse<T>` 래퍼로 성공 응답을 반환한다. 예외는 도메인별 예외와 공통 예외 계층을 사용한다.

주요 오류 유형:

- 존재하지 않는 사용자, 관리자, 상품, 옵션 variant, 주문, 장바구니, 배송지, 옵션 템플릿
- 중복 사용자 email
- 중복 관리자 email
- 잘못된 입력
- 재고 부족
- 취소 불가능한 주문 상태
- 상품 파일 업로드 개수 초과

## 인증 및 보안

- 사용자와 관리자는 별도 로그인 provider/filter/detail service를 가진다.
- JWT 기반 인증을 사용한다.
- `@AuthenticationPrincipal Long userId`로 인증된 사용자 PK를 받는 API가 있다.
- 관리자 전용 API는 `hasRole('ADMIN')` 또는 `ROLE_ADMIN` 권한을 요구한다.
- 사용자 전용 API는 `ROLE_USER` 권한을 요구한다.
- 배송지 API는 `ROLE_USER`, `ROLE_ADMIN` 둘 다 접근 가능하다.

## JWT refresh

Endpoint: `POST /client`

처리:

- refresh token 요청으로 새 token을 발급한다.
- token 인증 실패 시 인증 예외를 반환한다.

