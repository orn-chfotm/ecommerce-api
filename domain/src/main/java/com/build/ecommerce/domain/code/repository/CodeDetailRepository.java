package com.build.ecommerce.domain.code.repository;

import com.build.ecommerce.domain.code.enetity.CodeDetail;

import java.util.List;
import java.util.Optional;

public interface CodeDetailRepository {

    CodeDetail registerCodeDetail(CodeDetail codeDetail);

    List<CodeDetail> findAll();

    Optional<CodeDetail> findById(Long id);

    Optional<Integer> findMaxSortOrder(Long codeGroupId, Long parentId);

    void shiftSortOrderUp(Long codeGroupId, Long parentId, int fromInclusive, int toExclusive);

    void shiftSortOrderDown(Long codeGroupId, Long parentId, int fromExclusive, int toInclusive);
}
