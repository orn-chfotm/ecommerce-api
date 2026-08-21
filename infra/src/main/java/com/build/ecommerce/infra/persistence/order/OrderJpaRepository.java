package com.build.ecommerce.infra.persistence.order;

import com.build.ecommerce.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface OrderJpaRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
