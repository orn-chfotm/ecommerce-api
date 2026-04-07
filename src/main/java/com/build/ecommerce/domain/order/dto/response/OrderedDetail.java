package com.build.ecommerce.domain.order.dto.response;

public record OrderedDetail(
        OrderedProductResponse orderedProductResponse,
        OrderedProductDeatilResponse orderedProductDeatilResponse
) {

    private static class OrderedDetailBuilder{
        private OrderedProductResponse orderedProductResponse;
        private OrderedProductDeatilResponse orderedProductDeatilResponse;

        private OrderedDetailBuilder orderedProductResponse(OrderedProductResponse orderedProductResponse) {
            this.orderedProductResponse = orderedProductResponse;
            return this;
        }

        private OrderedDetailBuilder orderedProductDeatilResponse(OrderedProductDeatilResponse orderedProductDeatilResponse) {
            this.orderedProductDeatilResponse = orderedProductDeatilResponse;
            return this;
        }

        private OrderedDetail builde() {
            return new OrderedDetail(orderedProductResponse, orderedProductDeatilResponse);
        }
    }

    public static OrderedDetailBuilder builder() {
        return new OrderedDetailBuilder();
    }

    public static OrderedDetail toDto(OrderedProductResponse orderedProductResponse,
                                      OrderedProductDeatilResponse orderedProductDeatilResponse) {
        return OrderedDetail.builder()
                .orderedProductResponse(orderedProductResponse)
                .orderedProductDeatilResponse(orderedProductDeatilResponse)
                .builde();
    }

}
