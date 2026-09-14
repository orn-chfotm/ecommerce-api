package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.build.ecommerce.domain.code.repository.CodeGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class CodeGroupRepositoryAdapter implements CodeGroupRepository {
    private final CodeGroupJpaRepository repository;

    @Override
    public CodeGroup registerCodeGroup(CodeGroup codeGroup) {
        return repository.save(codeGroup);
    }

    @Override
    public List<CodeGroup> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CodeGroup> getCodeGroupDetail(Long id) {
        return repository.findById(id);
    }
}
