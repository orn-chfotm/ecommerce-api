package com.build.ecommerce.infra.persistence.order;

import com.build.ecommerce.domain.order.entity.OrderProduct;
import com.build.ecommerce.domain.order.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
class OrderProductRepositoryAdapter implements OrderProductRepository {

    private final OrderProductJpaRepository jpaRepository;

    @Override
    public List<OrderProduct> findAllByProductId(Long productId) {
        return jpaRepository.findAllByProductId(productId);
    }
}
