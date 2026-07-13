# 찜 PRD

## 문서 범위

현재 구현된 상품 찜 등록/조회/삭제 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/product/controller/ProductWishController.java`
- `src/main/java/com/build/ecommerce/domain/product/service/ProductWishService.java`
- `src/main/java/com/build/ecommerce/domain/product/dto/request/ProductWishRequest.java`
- `src/main/java/com/build/ecommerce/domain/product/entity/ProductWish.java`

## 찜 등록

Endpoint: `POST /v1/wish`

입력:

- `productId`: 필수

처리:

- 인증 사용자와 상품을 조회해 찜을 생성한다.
- 현재 코드에는 동일 사용자/상품 중복 찜 방지 로직이 없다.

## 찜 목록 조회

Endpoint: `GET /v1/wish`

처리:

- 인증 사용자의 찜 목록을 조회한다.
- 상품 파일 정보까지 함께 반환한다.

## 찜 상세 조회

Endpoint: `GET /v1/wish/{productWishId}`

처리:

- 인증 사용자 소유의 찜만 조회한다.
- 없으면 `ProductWishNotFoundException`

## 찜 삭제

Endpoint: `DELETE /v1/wish/{productWishId}`

처리:

- 인증 사용자 소유의 찜만 삭제한다.
- 없으면 `ProductWishNotFoundException`

