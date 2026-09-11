package com.build.ecommerce.domain.code.service;

import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupSearchRequest;
import com.build.ecommerce.domain.code.dto.response.CodeGroupResponse;
import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.build.ecommerce.domain.code.repository.CodeDetailRepository;
import com.build.ecommerce.domain.code.repository.CodeGroupRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CodeService {
    private final CodeGroupRepository codeGroupRepository;
    private final CodeDetailRepository codeDetailRepository;

    public CodeGroupResponse registerCodeGroup(@Valid CodeGroupRegisterRequest codeGroupRequest) {
        CodeGroup codeGroup = codeGroupRepository.registerCodeGroup(codeGroupRequest.toEntity());
        return CodeGroupResponse.toDto(codeGroup);
    }

    public CodeGroupResponse getCodeGroupList(@Valid CodeGroupSearchRequest request) {
        return null;
    }

    public CodeGroupResponse getCodeGroupDetail(@Valid CodeGroupSearchRequest request) {
        return null;
    }
}
