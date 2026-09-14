package com.build.ecommerce.domain.code.repository;

import com.build.ecommerce.domain.code.enetity.CodeGroup;

import java.util.List;
import java.util.Optional;

public interface CodeGroupRepository {

    CodeGroup registerCodeGroup(CodeGroup codeGroup);

    List<CodeGroup> findAll();

    Optional<CodeGroup> getCodeGroupDetail(Long id);
}
