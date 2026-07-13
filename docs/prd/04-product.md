# 상품 PRD

## 문서 범위

현재 구현된 상품 등록/조회/삭제 기능만 정리한다. 상품 옵션과 파일 업로드 상세 규칙은 별도 문서에서 다룬다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/product/controller/ProductController.java`
- `src/main/java/com/build/ecommerce/domain/product/service/ProductService.java`
- `src/main/java/com/build/ecommerce/domain/product/dto/request/ProductRequest.java`
- `src/main/java/com/build/ecommerce/domain/product/dto/request/ProductSearchRequest.java`
- `src/main/java/com/build/ecommerce/domain/product/entity/Product.java`

## 상품 등록

Endpoint: `POST /v1/product`

권한:

- `ADMIN`

입력:

- `category`: 필수, `FASHION`, `BEAUTY`, `FOOD`, `DIGITAL`, `TOY`
- `name`: 필수, 최대 200자
- `description`: 최대 2000자
- `price`: 필수, 0 이상
- `stockQuantity`: 필수, 0 이상
- `minOrderQuantity`: 필수, 1 이상
- `active`: 필수
- `files`: 선택, 상품 기준 최대 2개

처리:

- 상품을 저장한다.
- 파일이 있으면 파일 저장소에 저장하고 `FileMaster`, `FileDetail`을 생성해 상품에 연결한다.
- 트랜잭션 rollback 시 저장된 파일을 삭제한다.

## 상품 목록 조회

Endpoint: `GET /v1/product`

검색 조건:

- `category`
- `name`
- `minPrice`
- `maxPrice`
- `stockQuantity`
- `Pageable`

처리:

- QueryDSL 기반 검색 결과를 page로 반환한다.
- 상품 이미지 파일이 있으면 함께 조회해 응답에 포함한다.

## 상품 상세 조회

Endpoint: `GET /v1/product/{productId}`

처리:

- 상품 PK로 조회
- 없으면 `ProductNotFoundException`

## 상품 삭제

Endpoint: `DELETE /v1/product/{productId}`

권한:

- `ADMIN`

처리:

- 물리 삭제가 아니라 `status = DELETED`, `delAt = now`로 soft delete 처리한다.

