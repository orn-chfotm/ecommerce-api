# 파일 업로드 및 재고 PRD

## 문서 범위

현재 구현된 상품 파일 업로드 설정과 재고 처리 규칙만 정리한다.

근거 파일:

- `src/main/java/com/build/ecommerce/domain/product/service/ProductService.java`
- `src/main/java/com/build/ecommerce/domain/order/service/OrderService.java`
- `src/main/java/com/build/ecommerce/domain/cart/service/CartService.java`
- `src/main/java/com/build/ecommerce/core/config/properties/FileUploadProperties.java`
- `src/main/resources/application.yml`

## 파일 업로드

상품 등록 시 파일 업로드를 지원한다.

현재 기본 설정:

- 상품 파일 최대 개수: 2
- 허용 확장자: `jpg`, `jpeg`, `png`, `gif`, `webp`, `pdf`
- multipart 최대 파일 크기: 10MB
- multipart 최대 요청 크기: 50MB
- 기본 로컬 저장 root path는 `application.yml`의 `file.upload.root-path`

처리:

- 파일이 있으면 파일 저장소에 저장하고 `FileMaster`, `FileDetail`을 생성해 상품에 연결한다.
- 트랜잭션 rollback 시 저장된 파일을 삭제한다.
- 상품 파일 개수가 제한을 초과하면 `FileUploadExceedLimitException`

## 재고 규칙

- 옵션 없는 상품은 `Product.stockQuantity`를 사용한다.
- 옵션 있는 상품은 `ProductOptionVariant.stockQuantity`를 사용한다.
- `stockQuantity == null`인 상품은 현재 엔티티 로직상 재고 차감/검증을 건너뛴다.
- 주문 생성 시 재고를 차감한다.
- 주문 취소 시 재고를 복원한다.
- 장바구니 추가/수정은 재고를 차감하지 않고 수량 검증만 한다.

