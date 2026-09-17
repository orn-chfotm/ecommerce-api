package com.build.ecommerce.domain.menu.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.menu.dto.reqeust.MenuGroupUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(
        name = "MENU_GROUP",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_menu_group_name_url", columnNames = {"MENU_GROUP_NAME", "GROUP_URL"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MenuGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_GROUP_ID")
    @Comment("메뉴 그룹 id")
    private Long id;

    @Column(name = "MENU_GROUP_NAME", nullable = false, length = 100)
    @Comment("메뉴 그룹 명")
    private String name;

    @Column(name = "GROUP_URL", length = 200)
    @Comment("메뉴 그룹 URL")
    private String url;

    @Column(nullable = false)
    @Comment("메뉴 정렬")
    private int sortOrder;

    @Column(nullable = false)
    @Comment("사용 여부")
    private boolean active;

    @Builder
    public MenuGroup(String name, String url, int sortOrder, boolean active) {
        this.name = name;
        this.url = url;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public void change(MenuGroupUpdateRequest request) {
        this.name = request.name();
        this.url = request.url();
        this.sortOrder = request.sortOrder();
        this.active = request.active();
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
