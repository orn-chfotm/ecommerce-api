package com.build.ecommerce.domain.code.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailSortOrderMoveRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailRegisterRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailUpdateRequest;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupSortOrderMoveRequest;
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
                codeDetailRegisterRequest.toEntity(codeGroup, parent, nextSortOrder)
        );
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

    public CodeDetailResponse moveCodeDetail(Long codeGroupId, Long codeDetailId, CodeDetailSortOrderMoveRequest moveRequest) {
        CodeDetail current = codeDetailRepository.findByIdAndCodeGroup_Id(codeGroupId, codeDetailId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));

        Long parentId = current.getParent() == null ? null : current.getParent().getId();
        Long targetParentId = moveRequest.targetParentId();
        int targetSortOrder = moveRequest.targetSortOrder();

        boolean parentChanges = targetParentId != null && !targetParentId.equals(parentId);
        if (parentChanges) {
            return moveCodeDetailToNewParent(codeGroupId, current, parentId, targetParentId, targetSortOrder);
        }
        return moveCodeDetailWithinParent(codeGroupId, current, parentId, targetSortOrder);
    }

    private CodeDetailResponse moveCodeDetailWithinParent(Long codeGroupId, CodeDetail current, Long parentId, int targetSortOrder) {
        int originalSortOrder = current.getSortOrder();

        int maxSortOrder = codeDetailRepository.findMaxSortOrder(codeGroupId, parentId).orElse(1);
        if (targetSortOrder < 1 || targetSortOrder > maxSortOrder) {
            throw new InvalidInputException(CodeExceptionCode.CODE_DETAIL_SORT_ORDER_OUT_OF_RANGE);
        }

        if (targetSortOrder == originalSortOrder) {
            return CodeDetailResponse.toDto(current);
        }

        if (targetSortOrder < originalSortOrder) {
            codeDetailRepository.shiftSortOrderUp(codeGroupId, parentId, targetSortOrder, originalSortOrder);
        } else {
            codeDetailRepository.shiftSortOrderDown(codeGroupId, parentId, originalSortOrder, targetSortOrder);
        }

        current.changeSortOrder(targetSortOrder);
        return CodeDetailResponse.toDto(current);
    }

    private CodeDetailResponse moveCodeDetailToNewParent(
            Long codeGroupId, CodeDetail current, Long originalParentId, Long targetParentId, int targetSortOrder
    ) {
        if (targetParentId.equals(current.getId())) {
            throw new InvalidInputException(CodeExceptionCode.CODE_DETAIL_TARGET_PARENT_SELF_REFERENCE);
        }

        if (codeDetailRepository.existsByParentId(current.getId())) {
            throw new BusinessException(CodeExceptionCode.CODE_DETAIL_HAS_CHILDREN);
        }

        CodeDetail targetParent = codeDetailRepository.findByIdAndCodeGroup_Id(codeGroupId, targetParentId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));

        if (targetParent.getDepth() != 0) {
            throw new InvalidInputException(CodeExceptionCode.CODE_DETAIL_TARGET_PARENT_INVALID_DEPTH);
        }

        int destinationMaxSortOrder = codeDetailRepository.findMaxSortOrder(codeGroupId, targetParentId).orElse(0);
        if (targetSortOrder < 1 || targetSortOrder > destinationMaxSortOrder + 1) {
            throw new InvalidInputException(CodeExceptionCode.CODE_DETAIL_SORT_ORDER_OUT_OF_RANGE);
        }

        // 원래 부모의 형제들: 빠진 자리를 당겨서 채운다.
        codeDetailRepository.shiftSortOrderDown(codeGroupId, originalParentId, current.getSortOrder(), Integer.MAX_VALUE);
        // 새 부모의 형제들: 삽입할 자리를 만든다.
        codeDetailRepository.shiftSortOrderUp(codeGroupId, targetParentId, targetSortOrder, Integer.MAX_VALUE);

        current.moveToParent(targetParent, targetSortOrder);
        return CodeDetailResponse.toDto(current);
    }

    public CodeGroupResponse moveCodeGroup(Long codeGroupId, CodeGroupSortOrderMoveRequest moveRequest) {
        CodeGroup codeGroup = codeGroupRepository.getCodeGroupDetail(codeGroupId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_GROUP_NOT_FOUND));

        int originalSortOrder = codeGroup.getSortOrder();
        int targetSortOrder = moveRequest.targetSortOrder();

        int maxSortOrder = codeGroupRepository.findMaxSortOrder().orElse(1);
        if (targetSortOrder < 1 || targetSortOrder > maxSortOrder) {
            throw new InvalidInputException(CodeExceptionCode.CODE_GROUP_SORT_ORDER_OUT_OF_RANGE);
        }

        if (targetSortOrder == originalSortOrder) {
            return CodeGroupResponse.toDto(codeGroup);
        }

        if (targetSortOrder < originalSortOrder) {
            codeGroupRepository.shiftSortOrderUp(targetSortOrder, originalSortOrder);
        } else {
            codeGroupRepository.shiftSortOrderDown(originalSortOrder, targetSortOrder);
        }

        codeGroup.changeSortOrder(targetSortOrder);
        return CodeGroupResponse.toDto(codeGroup);
    }

    public void deleteCodeGroup(Long codeGroupId) {
        CodeGroup codeGroup = codeGroupRepository.getCodeGroupDetail(codeGroupId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_GROUP_NOT_FOUND));

        if (codeDetailRepository.existsByCodeGroup_Id(codeGroupId)) {
            throw new BusinessException(CodeExceptionCode.CODE_GROUP_DELETE_HAS_CHILDREN);
        }

        codeGroupRepository.shiftSortOrderDown(codeGroup.getSortOrder(), Integer.MAX_VALUE);
        codeGroupRepository.deleteCodeGroup(codeGroup);
    }

    public void deleteCodeDetail(Long codeGroupId, Long codeDetailId) {
        CodeDetail codeDetail = codeDetailRepository.findByIdAndCodeGroup_Id(codeGroupId, codeDetailId)
                .orElseThrow(() -> new NotFoundException(CodeExceptionCode.CODE_DETAIL_NOT_FOUND));

        if (codeDetailRepository.existsByParentId(codeDetailId)) {
            throw new BusinessException(CodeExceptionCode.CODE_DETAIL_DELETE_HAS_CHILDREN);
        }

        Long parentId = codeDetail.getParent() == null ? null : codeDetail.getParent().getId();
        codeDetailRepository.shiftSortOrderDown(codeGroupId, parentId, codeDetail.getSortOrder(), Integer.MAX_VALUE);
        codeDetailRepository.deleteCodeDetail(codeDetail);
    }
}
