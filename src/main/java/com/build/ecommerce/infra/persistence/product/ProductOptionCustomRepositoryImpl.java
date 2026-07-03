package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOption;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.build.ecommerce.domain.product.entity.QProductOption.productOption;
import static com.build.ecommerce.domain.product.entity.QProductOptionValue.productOptionValue;

@RequiredArgsConstructor
class ProductOptionCustomRepositoryImpl implements ProductOptionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductOption> findAllWithValuesByProductId(Long productId) {
        return jpaQueryFactory.selectFrom(productOption)
                .distinct()
                .leftJoin(productOption.productOptionValues, productOptionValue).fetchJoin()
                .where(productOption.product.id.eq(productId))
                .fetch();
    }
}
