package com.build.ecommerce.domain.code.enetity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.code.dto.reqeust.CodeGroupUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "CODE_GROUP",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_code_group_code", columnNames = {"GROUP_CODE"})
    }
)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "CMS 공통 코드 그룹 테이블", on = "TABLE")
public class CodeGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODE_GROUP_ID")
    @Comment("코드 id")
    private Long id;

    @Column(name = "GROUP_CODE", nullable = false, length=50, updatable = false)
    @Comment("코드 값")
    private String code;

    @Column(name = "GROUP_NAME", nullable = false, length = 100)
    @Comment("코드 명")
    private String name;

    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("코드 정렬")
    private int sortOrder;

    @Column(nullable = false)
    @Comment("코드 사용 여부")
    private boolean active;

    @Builder
    public CodeGroup(String code, String name, int sortOrder, boolean active) {
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public void change(CodeGroupUpdateRequest request) {
        this.name = request.name();
        this.sortOrder = request.sortOrder();
        this.active = request.active();
    }
}