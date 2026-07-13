# 주문 PRD

## 문서 범위

현재 구현된 주문 생성/조회/취소 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/order/**`
- `src/main/java/com/build/ecommerce/domain/product/entity/Product.java`
- `src/main/java/com/build/ecommerce/domain/product/entity/ProductOptionVariant.java`

## 주문 생성

Endpoint: `POST /v1/order`

권한:

- `USER`

입력:

- `addressId`: 필수
- `orders`: 필수, 1개 이상
- `orders[].productId`: 필수
- `orders[].productOptionVariantId`: 옵션 상품이면 필수, 옵션 없는 상품이면 지정 불가
- `orders[].quantity`: 필수, 1 이상

처리:

- 인증 사용자와 사용자 소유 배송지를 조회한다.
- 주문 상태는 `COMPLETE`로 생성한다.
- 옵션 상품은 `ProductOptionVariant`를 pessimistic lock 조회 후 재고를 차감한다.
- 옵션 없는 상품은 `Product`를 pessimistic lock 조회 후 재고를 차감한다.
- 주문 상품별 금액은 `(상품 가격 + variant 추가 금액) * 수량`이다.
- 주문 총액은 주문 상품 금액의 합계이다.

## 주문 목록 조회

Endpoint: `GET /v1/order`

권한:

- `USER`

처리:

- 인증 사용자의 주문 목록을 page로 조회한다.
- 주문 상품과 선택 옵션 정보를 함께 반환한다.

## 주문 상세 조회

Endpoint: `GET /v1/order/{orderId}`

권한:

- `USER`

처리:

- 인증 사용자 소유 주문만 조회한다.
- 없으면 `OrderNotFoundException`

## 주문 취소

Endpoint: `PATCH /v1/order/{orderId}`

권한:

- `USER`

처리:

- 인증 사용자 소유 주문만 취소한다.
- 현재 취소 가능 상태는 `COMPLETE`뿐이다.
- 취소 시 상태를 `CANCEL`로 변경한다.
- 주문 상품 재고를 복원한다.
- 취소 불가능한 상태면 `OrderStatusException`

