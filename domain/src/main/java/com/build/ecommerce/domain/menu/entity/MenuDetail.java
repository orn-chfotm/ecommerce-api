package com.build.ecommerce.domain.menu.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "MENU_DETAIL",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_menu_detail_name_url", columnNames = {"MENU_DETAIL_NAME", "URL"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MenuDetail extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_DETAIL_ID")
    @Comment("메뉴 상세 id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "MENU_GROUP_ID", nullable = false, updatable = false)
    private MenuGroup menuGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_DETAIL_ID")
    @Comment("상위 상세 코드 (최상위는 null)")
    private MenuDetail parent;

    @Column(nullable = false)
    private int depth;

    @Column(name = "MENU_DETAIL_NAME", nullable = false, length = 100)
    @Comment("메뉴 상세 명")
    private String name;

    @Column(length = 200)
    @Comment("메뉴 상세 URL")
    private String url;

    @Column(nullable = false)
    @Comment("메뉴 정렬")
    private int sortOrder;

    @Column(nullable = false)
    @Comment("사용 여부")
    private boolean active;

    @Builder
    public MenuDetail(MenuGroup menuGroup, MenuDetail parent, String name, String url, int sortOrder, boolean active) {
        this.menuGroup = menuGroup;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth + 1;
        this.name = name;
        this.url = url;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public void update(MenuDetailUpdateRequest request) {
        this.name = request.name();
        this.url = request.url();
        this.sortOrder = request.sortOrder();
        this.active = request.active();
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void moveToParent(MenuDetail newParent, int sortOrder) {
        this.parent = newParent;
        this.depth = newParent == null ? 0 : newParent.depth + 1;
        this.sortOrder = sortOrder;
    }
}
