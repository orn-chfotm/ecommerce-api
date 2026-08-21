package com.build.ecommerce.domain.order.repository;

import com.build.ecommerce.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Page<Long> findIdsByUserId(Long userId, Pageable pageable);

    List<Order> findAllDetailsByIds(List<Long> ids);

    Optional<Order> findDetailByIdAndUserId(Long orderId, Long userId);
}
