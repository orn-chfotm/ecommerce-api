package com.build.ecommerce.infra.persistence.order;

import com.build.ecommerce.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

interface OrderCustomRepository {

    Page<Long> findIdsByUserId(Long userId, Pageable pageable);

    List<Order> findAllDetailsByIds(List<Long> ids);

    Optional<Order> findDetailByIdAndUserId(Long orderId, Long userId);
}
