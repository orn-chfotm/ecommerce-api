package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOptionVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionVariantRepository extends JpaRepository<ProductOptionVariant, Long>, ProductOptionVariantCustomRepository {
}
