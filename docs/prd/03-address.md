# 배송지 PRD

## 문서 범위

현재 구현된 사용자 배송지 조회/등록 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/address/**`

## 배송지 목록 조회

Endpoint: `GET /v1/address`

권한:

- `ROLE_USER`
- `ROLE_ADMIN`

처리:

- 인증 principal의 userId로 사용자 조회
- 사용자에 연결된 배송지 목록 반환
- 사용자가 없으면 `UserNotFoundException`

## 배송지 등록

Endpoint: `POST /v1/address`

권한:

- `ROLE_USER`
- `ROLE_ADMIN`

입력:

- `addressType`: 필수, `REGION_ADDR` 또는 `ROAD_ADDR`
- `address`: 필수
- `extraAddress`: 필수
- `zipCode`: 필수

처리:

- 인증 사용자에 배송지를 추가
- 배송지 정보는 `AddressInfo` embedded value로 저장

