package com.build.ecommerce.infra.persistence.product;

import com.build.ecommerce.domain.product.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long>, ProductOptionCustomRepository {
}
