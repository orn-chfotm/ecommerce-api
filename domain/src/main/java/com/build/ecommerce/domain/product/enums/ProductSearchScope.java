package com.build.ecommerce.domain.product.enums;

/**
 * 상품 목록 조회 범위. 클라이언트 입력이 아니라 서버(Service)가 호출 경로에 따라 결정한다.
 */
public enum ProductSearchScope {

    // 관리자용: 상품 유형/노출 조건 필터 없이 전체 상품을 조회한다.
    ADMIN,

    // 사용자용: 일반 상품(NORMAL)만, 노출 게이트를 통과한 상품만 조회한다.
    USER
}
