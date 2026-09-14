package com.build.ecommerce.infra.persistence.code;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.repository.CodeDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class CodeDetailRepositoryAdapter implements CodeDetailRepository {
    private final CodeDetailJpaRepository repository;

    @Override
    public CodeDetail registerCodeDetail(CodeDetail codeDetail) {
        return repository.save(codeDetail);
    }

    @Override
    public List<CodeDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CodeDetail> findById(Long id) {
        return repository.findById(id);
    }
}
