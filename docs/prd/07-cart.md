# 장바구니 PRD

## 문서 범위

현재 구현된 장바구니 등록/조회/수정/삭제 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/cart/**`
- `src/main/java/com/build/ecommerce/domain/product/entity/Product.java`
- `src/main/java/com/build/ecommerce/domain/product/entity/ProductOptionVariant.java`

## 장바구니 추가

Endpoint: `POST /v1/cart`

권한:

- `USER`

입력:

- `productId`: 필수
- `productOptionVariantId`: 옵션 상품이면 필수, 옵션 없는 상품이면 지정 불가
- `quantity`: 필수, 양수

처리:

- 같은 사용자, 상품, variant 조합의 장바구니가 있으면 수량을 더한다.
- 상품에 옵션이 있으면 variant 선택이 필수이다.
- 상품에 옵션이 없으면 variant를 지정할 수 없다.
- 요청 누적 수량이 재고보다 많으면 `ProductNotEnoughStockException`

## 장바구니 조회

Endpoint: `GET /v1/cart`

처리:

- 인증 사용자의 장바구니 목록을 조회한다.
- variant가 있으면 선택 옵션 값을 함께 반환한다.

## 장바구니 수량 수정

Endpoint: `PATCH /v1/cart/{cartId}`

입력:

- `quantity`: 필수, 양수

처리:

- 인증 사용자 소유 장바구니만 수정한다.
- 변경 수량이 재고보다 많으면 `ProductNotEnoughStockException`

## 장바구니 단건 삭제

Endpoint: `DELETE /v1/cart/{cartId}`

처리:

- 인증 사용자 소유 장바구니만 삭제한다.
- 없으면 `CartNotFoundException`

## 장바구니 전체 삭제

Endpoint: `DELETE /v1/cart`

처리:

- 인증 사용자의 모든 장바구니를 삭제한다.

