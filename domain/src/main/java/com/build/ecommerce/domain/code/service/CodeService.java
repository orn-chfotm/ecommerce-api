package com.build.ecommerce.domain.code.service;

import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailUpdateRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupUpdateRequest;
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

        int nextSortOrder = codeDetailRepository.findMaxSortOrder(codeGroupId, codeDetailRegisterRequest.parentId())
                .map(max -> max + 1)
                .orElse(1);

        CodeDetail codeDetail = codeDetailRepository.registerCodeDetail(
                codeDetailRegisterRequest.toEntity(codeGroup, parent, nextSortOrder));
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

    public CodeGroupResponse updateCodeGroup(Long codeGroupId, CodeGroupUpdateRequest codeGroupUpdateRequest) {
        CodeGroup codeGroup = codeGroupRepository.getCodeGroupDetail(codeGroupId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_GROUP_NOT_FOUND));
        codeGroup.change(codeGroupUpdateRequest);
        return CodeGroupResponse.toDto(codeGroup);
    }

    public CodeDetailResponse updateCodeDetail(Long codeGroupId, Long codeDetailId, CodeDetailUpdateRequest codeDetailUpdateRequest) {
        CodeDetail codeDetail = codeDetailRepository.findByIdAndCodeGroup_Id(codeGroupId, codeDetailId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));

        codeDetail.update(codeDetailUpdateRequest);
        return CodeDetailResponse.toDto(codeDetail);
    }

    public CodeDetailResponse moveCodeDetail(Long codeGroupId, Long codeDetailId, CodeDetailOrderMoveRequest moveRequest) {
        CodeDetail current = codeDetailRepository.findByIdAndCodeGroup_Id(codeGroupId, codeDetailId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));

        Long currentCodeGroupId = current.getCodeGroup().getId();
        Long parentId = current.getParent() == null ? null : current.getParent().getId();
        int originalSortOrder = current.getSortOrder();
        int targetSortOrder = moveRequest.targetSortOrder();

        int maxSortOrder = codeDetailRepository.findMaxSortOrder(currentCodeGroupId, parentId).orElse(1);
        if (targetSortOrder < 1 || targetSortOrder > maxSortOrder) {
            throw new InvalidInputException(CodeExceptionCode.CODE_DETAIL_SORT_ORDER_OUT_OF_RANGE);
        }

        if (targetSortOrder == originalSortOrder) {
            return CodeDetailResponse.toDto(current);
        }

        if (targetSortOrder < originalSortOrder) {
            codeDetailRepository.shiftSortOrderUp(currentCodeGroupId, parentId, targetSortOrder, originalSortOrder);
        } else {
            codeDetailRepository.shiftSortOrderDown(currentCodeGroupId, parentId, originalSortOrder, targetSortOrder);
        }

        current.changeSortOrder(targetSortOrder);
        return CodeDetailResponse.toDto(current);
    }
}
