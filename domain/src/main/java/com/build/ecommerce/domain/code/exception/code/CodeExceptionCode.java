package com.build.ecommerce.domain.code.exception.code;

import com.build.ecommerce.core.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum CodeExceptionCode implements ErrorCode {

    CODE_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "코드 그룹 정보를 찾을 수 없습니다."),
    CODE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "상세 코드 정보를 찾을 수 없습니다."),
    CODE_DETAIL_SORT_ORDER_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "정렬 순서 범위를 확인해주세요."),
    CODE_GROUP_SORT_ORDER_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "정렬 순서 범위를 확인해주세요."),
    CODE_DETAIL_HAS_CHILDREN(HttpStatus.CONFLICT, "자식 코드가 있는 상세 코드는 다른 부모로 이동할 수 없습니다."),
    CODE_DETAIL_TARGET_PARENT_INVALID_DEPTH(HttpStatus.BAD_REQUEST, "이동 대상 부모는 최상위 상세 코드여야 합니다."),
    CODE_DETAIL_TARGET_PARENT_SELF_REFERENCE(HttpStatus.BAD_REQUEST, "자기 자신을 부모로 지정할 수 없습니다."),
    CODE_GROUP_DELETE_HAS_CHILDREN(HttpStatus.CONFLICT, "하위 상세 코드가 있는 코드 그룹은 삭제할 수 없습니다."),
    CODE_DETAIL_DELETE_HAS_CHILDREN(HttpStatus.CONFLICT, "자식 코드가 있는 상세 코드는 삭제할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
