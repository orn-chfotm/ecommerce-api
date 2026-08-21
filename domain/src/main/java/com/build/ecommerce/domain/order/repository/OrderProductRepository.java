package com.build.ecommerce.domain.order.repository;

import com.build.ecommerce.domain.order.entity.OrderProduct;

import java.util.List;

public interface OrderProductRepository {

    List<OrderProduct> findAllByProductId(Long productId);
}
