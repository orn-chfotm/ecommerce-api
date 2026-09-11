package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.build.ecommerce.domain.code.repository.CodeGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CodeGroupRepositoryAdapter implements CodeGroupRepository {
    private final CodeGroupJpaRepository repository;

    @Override
    public CodeGroup registerCodeGroup(CodeGroup codeGroup) {
        return repository.save(codeGroup);
    }
}
