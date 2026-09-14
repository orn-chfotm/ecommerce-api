package com.build.ecommerce.domain.code.repository;

import com.build.ecommerce.domain.code.enetity.CodeDetail;

import java.util.List;
import java.util.Optional;

public interface CodeDetailRepository {

    CodeDetail registerCodeDetail(CodeDetail codeDetail);

    List<CodeDetail> findAll();

    Optional<CodeDetail> findById(Long id);
}
