package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductAddOn;
import com.build.ecommerce.domain.product.repository.ProductAddOnRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProduct.product;
import static com.build.ecommerce.domain.product.entity.QProductAddOn.productAddOn;

@Repository
@RequiredArgsConstructor
class ProductAddOnRepositoryAdapter implements ProductAddOnRepository {

    private final ProductAddOnJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProductAddOn save(ProductAddOn productAddOnEntity) {
        return jpaRepository.save(productAddOnEntity);
    }

    @Override
    public List<ProductAddOn> findAllByProductId(Long productId) {
        return jpaQueryFactory.selectFrom(productAddOn)
                .join(productAddOn.addOnProduct, product).fetchJoin()
                .where(productAddOn.product.id.eq(productId))
                .orderBy(productAddOn.sortOrder.asc())
                .fetch();
    }

    @Override
    public boolean existsByProductIdAndAddOnProductId(Long productId, Long addOnProductId) {
        Integer result = jpaQueryFactory.selectOne()
                .from(productAddOn)
                .where(
                        productAddOn.product.id.eq(productId),
                        productAddOn.addOnProduct.id.eq(addOnProductId)
                )
                .fetchFirst();
        return result != null;
    }

    @Override
    public Optional<Integer> findMaxSortOrderByProductId(Long productId) {
        return Optional.ofNullable(
                jpaQueryFactory.select(productAddOn.sortOrder.max())
                        .from(productAddOn)
                        .where(productAddOn.product.id.eq(productId))
                        .fetchOne()
        );
    }

    @Override
    public List<ProductAddOn> findAllByProductIdAndAddOnProductIdIn(Long productId, List<Long> addOnProductIds) {
        return jpaQueryFactory.selectFrom(productAddOn)
                .join(productAddOn.addOnProduct, product).fetchJoin()
                .where(
                        productAddOn.product.id.eq(productId),
                        productAddOn.addOnProduct.id.in(addOnProductIds)
                )
                .orderBy(productAddOn.sortOrder.asc())
                .fetch();
    }
}
