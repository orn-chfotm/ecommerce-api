package com.build.ecommerce.domain.order.dto.response;

public record OrderedDetail(
        OrderedProductResponse orderedProductResponse,
        OrderedProductDetailResponse orderedProductDetailResponse
) {

    private static class OrderedDetailBuilder{
        private OrderedProductResponse orderedProductResponse;
        private OrderedProductDetailResponse orderedProductDetailResponse;

        private OrderedDetailBuilder orderedProductResponse(OrderedProductResponse orderedProductResponse) {
            this.orderedProductResponse = orderedProductResponse;
            return this;
        }

        private OrderedDetailBuilder orderedProductDeatilResponse(OrderedProductDetailResponse orderedProductDetailResponse) {
            this.orderedProductDetailResponse = orderedProductDetailResponse;
            return this;
        }

        private OrderedDetail builde() {
            return new OrderedDetail(orderedProductResponse, orderedProductDetailResponse);
        }
    }

    public static OrderedDetailBuilder builder() {
        return new OrderedDetailBuilder();
    }

    public static OrderedDetail toDto(OrderedProductResponse orderedProductResponse,
                                      OrderedProductDetailResponse orderedProductDetailResponse) {
        return OrderedDetail.builder()
                .orderedProductResponse(orderedProductResponse)
                .orderedProductDeatilResponse(orderedProductDetailResponse)
                .builde();
    }

}
