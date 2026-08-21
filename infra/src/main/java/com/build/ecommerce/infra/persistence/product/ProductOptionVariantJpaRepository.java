package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductOptionVariantJpaRepository extends JpaRepository<ProductOptionVariant, Long> {
}
