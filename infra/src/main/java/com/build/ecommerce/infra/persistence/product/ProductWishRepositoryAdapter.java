package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.product.repository.ProductWishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class ProductWishRepositoryAdapter implements ProductWishRepository {

    private final ProductWishJpaRepository jpaRepository;

    @Override
    public ProductWish save(ProductWish productWish) {
        return jpaRepository.save(productWish);
    }

    @Override
    public List<ProductWish> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<ProductWish> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public void delete(ProductWish productWish) {
        jpaRepository.delete(productWish);
    }

    @Override
    public int deleteProductWishByIdAndUserId(Long id, Long userId) {
        return jpaRepository.deleteProductWishByIdAndUserId(id, userId);
    }
}
