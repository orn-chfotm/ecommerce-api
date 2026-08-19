package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import com.build.ecommerce.domain.product.entity.ProductOptionVariantValue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProductOption.productOption;
import static com.build.ecommerce.domain.product.entity.QProductOptionValue.productOptionValue;
import static com.build.ecommerce.domain.product.entity.QProductOptionVariant.productOptionVariant;
import static com.build.ecommerce.domain.product.entity.QProductOptionVariantValue.productOptionVariantValue;

@RequiredArgsConstructor
class ProductOptionVariantCustomRepositoryImpl implements ProductOptionVariantCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
