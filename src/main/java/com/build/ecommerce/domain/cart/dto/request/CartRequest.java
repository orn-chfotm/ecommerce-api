package com.build.ecommerce.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartRequest(
        @NotNull Long productId,
        @NotNull @Positive int quantity
) {
}
