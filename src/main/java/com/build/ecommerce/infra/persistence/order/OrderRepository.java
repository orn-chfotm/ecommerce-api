package com.build.ecommerce.infra.persistence.order;

import com.build.ecommerce.domain.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"orderProducts", "orderProducts.product"})
    @Query("""
            select distinct o
            from Order o
            where o.user.id = :userId
            """)
    List<Order> findAllDetailsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"orderProducts", "orderProducts.product"})
    @Query("""
            select o
            from Order o
            where o.id = :orderId
              and o.user.id = :userId
            """)
    Optional<Order> findDetailByIdAndUserId(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );
}
