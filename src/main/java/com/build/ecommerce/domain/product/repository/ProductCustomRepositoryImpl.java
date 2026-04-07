package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.product.entity.QProduct.product;

@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final JPAQueryFactory queryFactory;

    public ProductCustomRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<List<Product>> customFind(ProductSearchRequest productSearchRequest) {
        return Optional.ofNullable(
                queryFactory.selectFrom(product)
                .where(
                        product.category.in(productSearchRequest.getCategory()),
                        product.name.like("% " + productSearchRequest.name() + " %"),
                        product.price.between(productSearchRequest.minPrice(), productSearchRequest.maxPrice()),
                        product.stockQuantity.gt(productSearchRequest.stockQuantity())
                )
                .fetch()
        );
    }
}
