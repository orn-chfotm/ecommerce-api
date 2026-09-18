package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductAddOn;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductAddOnJpaRepository extends JpaRepository<ProductAddOn, Long> {
}
