package com.build.ecommerce.domain.product.repository;

import com.build.ecommerce.domain.product.entity.ProductWish;
import com.build.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductWishRepository extends JpaRepository<ProductWish, Long> {

    @Query("""
        select pw
        from ProductWish pw
        join fetch pw.product p
        where pw.user.id = :userId""")
    List<ProductWish> findByUserId(Long userId);

    Optional<ProductWish> findByIdAndUserId(Long id, Long userId);

    int deleteProductWishByIdAndUserId(Long id, Long userId);
}
