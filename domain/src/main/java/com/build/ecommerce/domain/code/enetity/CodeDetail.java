package com.build.ecommerce.domain.code.enetity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.code.dto.reqeust.CodeDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;

@Entity
@Table(
        name = "CODE_DETAIL",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_code_detail_code",
                columnNames = {"CODE_GROUP_ID", "DETAIL_CODE"}
        )
)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "CMS 공통 코드 그룹 테이블", on = "TABLE")
public class CodeDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODE_DETAIL_ID")
    @Comment("코드 id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CODE_GROUP_ID", nullable = false, updatable = false)
    private CodeGroup codeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_DETAIL_ID", updatable = false)
    @Comment("상위 상세 코드 (최상위는 null)")
    private CodeDetail parent;

    @Column(name = "DEPTH", nullable = false, updatable = false)
    @Comment("트리 깊이 (최상위 0)")
    private int depth;

    @Column(name = "DETAIL_CODE", nullable = false, length = 50, updatable = false)
    @Comment("코드 값")
    private String code;

    @Column(name = "DETAIL_NAME", nullable = false, length = 100)
    @Comment("코드 명")
    private String name;

    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("코드 정렬")
    private int sortOrder;

    @Column(nullable = false)
    @Comment("코드 사용 여부")
    private boolean active;

    @Builder
    public CodeDetail(CodeGroup codeGroup, CodeDetail parent, String code, String name, int sortOrder, boolean active) {
        this.codeGroup = codeGroup;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth + 1;
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public void update(CodeDetailUpdateRequest request) {
        this.name = request.name();
        this.sortOrder = request.sortOrder();
        this.active = request.active();
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}