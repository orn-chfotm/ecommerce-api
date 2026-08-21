package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
