package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;
import com.build.ecommerce.domain.product.repository.ProductOptionVariantRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProductOption.productOption;
import static com.build.ecommerce.domain.product.entity.QProductOptionValue.productOptionValue;
import static com.build.ecommerce.domain.product.entity.QProductOptionVariant.productOptionVariant;
import static com.build.ecommerce.domain.product.entity.QProductOptionVariantValue.productOptionVariantValue;

@Repository
@RequiredArgsConstructor
class ProductOptionVariantRepositoryAdapter implements ProductOptionVariantRepository {

    private final ProductOptionVariantJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProductOptionVariant save(ProductOptionVariant productOptionVariantEntity) {
        return jpaRepository.save(productOptionVariantEntity);
    }

    @Override
    public Optional<ProductOptionVariant> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ProductOptionVariant> findAllWithValuesByProductId(Long productId) {
        return jpaQueryFactory.selectFrom(productOptionVariant)
                .distinct()
                .leftJoin(productOptionVariant.productOptionVariantValues, productOptionVariantValue).fetchJoin()
                .leftJoin(productOptionVariantValue.productOption, productOption).fetchJoin()
                .leftJoin(productOptionVariantValue.productOptionValue, productOptionValue).fetchJoin()
                .where(productOptionVariant.product.id.eq(productId))
                .fetch();
    }

    @Override
    public Optional<ProductOptionVariant> findByIdForUpdate(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(productOptionVariant)
                        .where(productOptionVariant.id.eq(id))
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .fetchOne()
        );
    }

    @Override
    public List<ProductOptionVariantValue> findVariantValuesByVariantIds(List<Long> variantIds) {
        return jpaQueryFactory.selectFrom(productOptionVariantValue)
                .join(productOptionVariantValue.productOption, productOption).fetchJoin()
                .join(productOptionVariantValue.productOptionValue, productOptionValue).fetchJoin()
                .where(productOptionVariantValue.productOptionVariant.id.in(variantIds))
                .fetch();
    }
}
