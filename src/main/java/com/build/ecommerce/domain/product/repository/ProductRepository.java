package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where (:category is null or p.category = :category) " +
            "and (:name is null or p.name like %:name%) " +
            "and (:minPrice is null or p.price >= :minPrice) " +
            "and (:maxPrice is null or p.price <= :maxPrice) " +
            "and (:stockQuantity is null or p.stockQuantity >= :stockQuantity)")
    List<Product> searchProducts(@Param("category") ProductCategoryType productCategoryType,
                                         @Param("name") String name,
                                         @Param("minPrice") Integer minPrice,
                                         @Param("maxPrice") Integer maxPrice,
                                         @Param("stockQuantity") Integer stockQuantity);
}
