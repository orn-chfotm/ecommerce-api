package com.build.ecommerce.domain.order.service;

import com.build.ecommerce.domain.order.dto.request.OrderAddOnDetail;
import com.build.ecommerce.domain.order.dto.request.OrderDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 락 대상 수집은 순수 함수이므로 Spring Context 없이 검증한다.
 * (데드락 자체를 재현하는 통합 테스트는 flaky 하므로 두지 않는다.)
 */
class OrderServiceLockTargetTest {

    @Test
    @DisplayName("잠글 제품 id는 요청 순서와 무관하게 오름차순으로 반환된다")
    void collectLockTargetProductIds_sortsAscending() {
        List<OrderDetail> orderDetails = List.of(
                new OrderDetail(30L, null, 1, null),
                new OrderDetail(10L, null, 1, null),
                new OrderDetail(20L, null, 1, null)
        );

        assertThat(OrderService.collectLockTargetProductIds(orderDetails))
                .containsExactly(10L, 20L, 30L);
    }

    @Test
    @DisplayName("잠글 제품 id의 중복은 제거된다")
    void collectLockTargetProductIds_removesDuplicates() {
        List<OrderDetail> orderDetails = List.of(
                new OrderDetail(20L, null, 1, null),
                new OrderDetail(10L, null, 1, null),
                new OrderDetail(20L, null, 2, null)
        );

        assertThat(OrderService.collectLockTargetProductIds(orderDetails))
                .containsExactly(10L, 20L);
    }

    @Test
    @DisplayName("추가구성상품 id도 본품 id와 함께 수집돼 전체가 오름차순·중복제거된다")
    void collectLockTargetProductIds_includesAddOnProductIds() {
        List<OrderDetail> orderDetails = List.of(
                new OrderDetail(50L, null, 1, List.of(
                        new OrderAddOnDetail(15L, 1),
                        new OrderAddOnDetail(80L, 2)
                )),
                new OrderDetail(30L, null, 1, List.of(
                        new OrderAddOnDetail(15L, 1)
                ))
        );

        assertThat(OrderService.collectLockTargetProductIds(orderDetails))
                .containsExactly(15L, 30L, 50L, 80L);
    }

    @Test
    @DisplayName("addOns가 null이어도 제품 id 수집은 예외 없이 동작한다")
    void collectLockTargetProductIds_allowsNullAddOns() {
        List<OrderDetail> orderDetails = List.of(new OrderDetail(10L, null, 1, null));

        assertThatCode(() -> OrderService.collectLockTargetProductIds(orderDetails))
                .doesNotThrowAnyException();
        assertThat(OrderService.collectLockTargetProductIds(orderDetails)).containsExactly(10L);
    }

    @Test
    @DisplayName("잠글 옵션 조합 id는 오름차순으로 중복 없이 반환된다")
    void collectLockTargetVariantIds_sortsAndRemovesDuplicates() {
        List<OrderDetail> orderDetails = List.of(
                new OrderDetail(1L, 300L, 1, null),
                new OrderDetail(2L, 100L, 1, null),
                new OrderDetail(3L, 300L, 1, null),
                new OrderDetail(4L, 200L, 1, null)
        );

        assertThat(OrderService.collectLockTargetVariantIds(orderDetails))
                .containsExactly(100L, 200L, 300L);
    }

    @Test
    @DisplayName("옵션 조합 id가 null인 주문 라인은 락 대상에서 제외된다")
    void collectLockTargetVariantIds_excludesNull() {
        List<OrderDetail> orderDetails = List.of(
                new OrderDetail(1L, null, 1, null),
                new OrderDetail(2L, 200L, 1, null),
                new OrderDetail(3L, null, 1, null)
        );

        assertThat(OrderService.collectLockTargetVariantIds(orderDetails))
                .containsExactly(200L);
    }

    @Test
    @DisplayName("addOns가 null이어도 옵션 조합 id 수집은 예외 없이 동작한다")
    void collectLockTargetVariantIds_allowsNullAddOns() {
        List<OrderDetail> orderDetails = List.of(new OrderDetail(1L, 100L, 1, null));

        assertThatCode(() -> OrderService.collectLockTargetVariantIds(orderDetails))
                .doesNotThrowAnyException();
        assertThat(OrderService.collectLockTargetVariantIds(orderDetails)).containsExactly(100L);
    }
}
