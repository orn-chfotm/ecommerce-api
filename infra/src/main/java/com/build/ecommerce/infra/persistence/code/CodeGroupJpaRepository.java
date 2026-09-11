package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeGroupJpaRepository extends JpaRepository<CodeGroup, Long> {
}
