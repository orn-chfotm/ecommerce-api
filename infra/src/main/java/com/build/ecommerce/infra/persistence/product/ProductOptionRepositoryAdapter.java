package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOption;
import com.build.ecommerce.domain.product.repository.ProductOptionRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.build.ecommerce.domain.product.entity.QProductOption.productOption;
import static com.build.ecommerce.domain.product.entity.QProductOptionValue.productOptionValue;

@Repository
@RequiredArgsConstructor
class ProductOptionRepositoryAdapter implements ProductOptionRepository {

    private final ProductOptionJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ProductOption save(ProductOption productOptionEntity) {
        return jpaRepository.save(productOptionEntity);
    }

    @Override
    public List<ProductOption> findAllWithValuesByProductId(Long productId) {
        return jpaQueryFactory.selectFrom(productOption)
                .distinct()
                .leftJoin(productOption.productOptionValues, productOptionValue).fetchJoin()
                .where(productOption.product.id.eq(productId))
                .fetch();
    }
}
