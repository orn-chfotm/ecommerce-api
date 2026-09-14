package com.build.ecommerce.domain.code.service;

import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.response.CodeDetailResponse;
import com.build.ecommerce.domain.code.dto.response.CodeGroupResponse;
import com.build.ecommerce.domain.code.dto.response.CodeTreeResponse;
import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.enetity.CodeGroup;
import com.build.ecommerce.domain.code.exception.code.CodeExceptionCode;
import com.build.ecommerce.domain.code.repository.CodeDetailRepository;
import com.build.ecommerce.domain.code.repository.CodeGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CodeService {
    private final CodeGroupRepository codeGroupRepository;
    private final CodeDetailRepository codeDetailRepository;

    public CodeGroupResponse registerCodeGroup(CodeGroupRegisterRequest codeGroupRegisterRequest) {
        CodeGroup codeGroup = codeGroupRepository.registerCodeGroup(codeGroupRegisterRequest.toEntity());
        return CodeGroupResponse.toDto(codeGroup);
    }

    public CodeDetailResponse registerCodeDetail(Long codeGroupId, CodeDetailRegisterRequest codeDetailRegisterRequest) {
        CodeGroup codeGroup = codeGroupRepository.getCodeGroupDetail(codeGroupId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_GROUP_NOT_FOUND));

        CodeDetail parent = null;
        if (codeDetailRegisterRequest.parentId() != null) {
            parent = codeDetailRepository.findById(codeDetailRegisterRequest.parentId())
                    .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));
        }

        CodeDetail codeDetail = codeDetailRepository.registerCodeDetail(
                codeDetailRegisterRequest.toEntity(codeGroup, parent));
        return CodeDetailResponse.toDto(codeDetail);
    }

    @Transactional(readOnly = true)
    public List<CodeTreeResponse> getCodeTree() {
        Map<Long, List<CodeDetail>> detailsByGroupId = codeDetailRepository.findAll().stream()
                .collect(Collectors.groupingBy(codeDetail -> codeDetail.getCodeGroup().getId()));

        return codeGroupRepository.findAll().stream()
                .map(group -> CodeTreeResponse.toTreeDto(
                        group, CodeTreeResponse.toDetailListDto(detailsByGroupId.getOrDefault(group.getId(), List.of()))))
                .toList();
    }

    @Transactional(readOnly = true)
    public CodeGroupResponse getCodeGroupDetail(Long codeGroupId) {
        CodeGroup codeGroup = codeGroupRepository.getCodeGroupDetail(codeGroupId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_GROUP_NOT_FOUND));
        return CodeGroupResponse.toDto(codeGroup);
    }

    @Transactional(readOnly = true)
    public CodeDetailResponse getCodeDetailDetail(Long codeDetailId) {
        CodeDetail codeDetail = codeDetailRepository.findById(codeDetailId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));
        return CodeDetailResponse.toDto(codeDetail);
    }
}
