package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeDetailJpaRepository extends JpaRepository<CodeDetail, Long> {
}
