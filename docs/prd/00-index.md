# PRD Index

이 디렉토리는 현재 구현된 ecommerce-be 백엔드 기준의 제품 요구사항 문서를 둔다.

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

## Claude Code 연동 근거

- Claude Code 공식 문서 기준, 프로젝트 공용 지침은 루트 `CLAUDE.md` 또는 `.claude/CLAUDE.md`에 둘 수 있다.
- `CLAUDE.md`는 `@path/to/file.md` 형식으로 추가 Markdown 파일을 import할 수 있다.
- import된 파일은 시작 컨텍스트에 함께 로드되므로, 전체 PRD를 매 세션 로드하지 않기 위해 루트 `CLAUDE.md`는 이 index만 import한다.
- 제품 요구사항 확인이 필요하면 관련 기능 문서만 직접 읽는다.
