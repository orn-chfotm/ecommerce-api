package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductCategoryType;
import com.build.ecommerce.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

    @Query("SELECT p FROM Product p " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:name IS NULL OR p.name LIKE %:name%) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:stockQuantity IS NULL OR p.stockQuantity >= :stockQuantity)")
    List<Product> searchProducts(@Param("category") ProductCategoryType productCategoryType,
                                         @Param("name") String name,
                                         @Param("minPrice") Integer minPrice,
                                         @Param("maxPrice") Integer maxPrice,
                                         @Param("stockQuantity") Integer stockQuantity);
}
