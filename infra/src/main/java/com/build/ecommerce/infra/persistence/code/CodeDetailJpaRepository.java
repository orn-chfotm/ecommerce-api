package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeDetailJpaRepository extends JpaRepository<CodeDetail, Long> {
    Optional<CodeDetail> findByIdAndCodeGroup_Id(Long codeDetailId, Long codeGroupId);

    boolean existsByParentId(Long parentId);

    boolean existsByCodeGroup_Id(Long codeGroupId);
}
