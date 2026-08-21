package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
}
