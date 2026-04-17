package com.build.ecommerce.domain.order.repository;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    List<OrderProduct> findAllByProductId(Long productId);
}
