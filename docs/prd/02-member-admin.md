# 회원 및 관리자 PRD

## 문서 범위

현재 구현된 회원과 관리자 가입/조회 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/user/**`
- `src/main/java/com/build/ecommerce/domain/admin/**`

## 회원

### 회원 가입

Endpoint: `POST /v1/user`

입력:

- `email`: 필수
- `password`: 필수
- `name`: 필수
- `gender`: 필수, `GenderType.getByValue`로 변환
- `birthDate`: 필수, 문자열을 `LocalDate`로 변환

처리:

- email 중복이면 `UserExistException`
- password는 `PasswordEncoder`로 암호화
- role은 `USER`로 저장

### 회원 정보 조회

Endpoint: `GET /v1/user`

처리:

- 인증 principal의 userId로 사용자 조회
- 없으면 `UserNotFoundException`

## 관리자

### 관리자 가입

Endpoint: `POST /v1/admin`

입력:

- `email`: 필수
- `password`: 필수
- `name`: 필수
- `role`: 필수, `AdminRoleType.getByValue`로 변환

처리:

- email 중복이면 `AdminExistException`
- password는 `PasswordEncoder`로 암호화

### 관리자 정보 조회

Endpoint: `GET /v1/admin`

처리:

- request body의 email로 관리자 조회
- `ROLE_ADMIN` 필요
- 없으면 `AdminNotFoundException`

