package com.build.ecommerce.domain.code.dto.reqeust;

import com.build.ecommerce.domain.code.enetity.CodeDetail;
import com.build.ecommerce.domain.code.enetity.CodeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CodeDetailRegisterRequest(
        @Schema(name = "부모 코드 (최상위인 경우 null)")
        Long parentId,
        @NotBlank(message = "코드 값을 확인해주세요.")
        @Size(max = 50, message = "코드 값은 50자 이하로 입력해주세요.")
        @Schema(name = "코드")
        String code,
        @NotBlank(message = "코드 명을 확인해주세요.")
        @Size(min = 1, max = 100, message = "코드 명은 최소 1자 이상 100자 이하를 입력해주세요.")
        @Schema(name = "코드 명")
        String name,
        @NotNull(message = "사용 여부를 확인해주세요.")
        @Schema(name = "사용 여부")
        Boolean active
) {
        public CodeDetail toEntity(CodeGroup codeGroup, CodeDetail parent, int sortOrder) {
               return CodeDetail.builder()
                       .codeGroup(codeGroup)
                       .parent(parent)
                       .code(this.code)
                       .name(this.name)
                       .sortOrder(sortOrder)
                       .active(this.active)
                       .build();
        }
}
