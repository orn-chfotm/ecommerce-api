# 상품 옵션 및 옵션 템플릿 PRD

## 문서 범위

현재 구현된 상품 옵션, SKU variant, 옵션 템플릿 기능만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/product/controller/ProductController.java`
- `src/main/java/com/build/ecommerce/domain/product/service/ProductOptionService.java`
- `src/main/java/com/build/ecommerce/domain/product/dto/request/ProductOption*.java`
- `src/main/java/com/build/ecommerce/domain/product/entity/ProductOption*.java`
- `src/main/java/com/build/ecommerce/domain/optiontemplate/**`

## 상품 옵션 등록

Endpoint: `POST /v1/product/{productId}/options`

권한:

- `ADMIN`

입력:

- `options`: 필수, 1개 이상
- `options[].name`: 필수
- `options[].sortOrder`: 필수, 0 이상
- `options[].values`: 필수, 1개 이상
- `variants`: 필수, 1개 이상
- `variants[].sku`: 선택, 최대 100자
- `variants[].stockQuantity`: 필수, 0 이상
- `variants[].priceDelta`: 선택, 0 이상, 미입력 시 0
- `variants[].maxPurchaseQuantity`: 선택, 1 이상
- `variants[].optionValues`: 필수, 1개 이상

처리:

- 상품이 없으면 `ProductNotFoundException`
- 이미 옵션이 등록된 상품이면 `ProductOptionAlreadyRegisteredException`
- 옵션 축과 옵션 값을 저장한다.
- variant별 SKU, 재고, 추가 금액, 최대 구매 수량, 옵션 값 조합을 저장한다.
- 등록 완료 시 상품의 `hasOptions`를 true로 변경한다.
- variant가 참조하는 옵션명/옵션값이 등록 요청 내에 없으면 `InvalidInputException`

## 상품 옵션 조회

Endpoint: `GET /v1/product/{productId}/options`

처리:

- 상품의 옵션 축, 옵션 값, variant 목록을 조회한다.

## 옵션 variant 재고 수정

Endpoint: `PATCH /v1/product/{productId}/options/variants/{variantId}/stock`

권한:

- `ADMIN`

입력:

- `stockQuantity`: 필수, 0 이상

처리:

- variant가 없거나 productId와 연결되지 않으면 `ProductOptionVariantNotFoundException`
- variant 재고 수량을 변경한다.

## 옵션 템플릿

옵션 템플릿은 상품 옵션 등록을 돕기 위한 독립적인 옵션 이름/값 묶음이다. 현재 상품 옵션 등록 로직과 직접 연결되지는 않는다.

### 옵션 템플릿 등록

Endpoint: `POST /v1/option-template`

권한:

- `ADMIN`

입력:

- `name`: 필수, 최대 100자
- `optionTemplateValues`: 필수, 1개 이상
- `optionTemplateValues[].value`: 필수, 최대 100자
- `optionTemplateValues[].sortOrder`: 필수, 0 이상

### 옵션 템플릿 목록/상세 조회

Endpoints:

- `GET /v1/option-template`
- `GET /v1/option-template/{optionTemplateId}`

처리:

- 목록 조회는 값 목록까지 함께 조회한다.
- 상세 대상이 없으면 `OptionTemplateNotFoundException`

### 옵션 템플릿 수정

Endpoint: `PATCH /v1/option-template/{optionTemplateId}`

권한:

- `ADMIN`

처리:

- 이름을 변경한다.
- 기존 값을 모두 제거하고 요청 값으로 다시 구성한다.

### 옵션 템플릿 삭제

Endpoint: `DELETE /v1/option-template/{optionTemplateId}`

권한:

- `ADMIN`

처리:

- 옵션 템플릿을 물리 삭제한다.

