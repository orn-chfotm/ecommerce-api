package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.repository.CodeDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CodeDetailRepositoryAdapter implements CodeDetailRepository {
    private final CodeDetailJpaRepository repository;

    @Override
    public CodeDetail registerCodeDetail(CodeDetail codeDetail) {
        return null;
    }
}
