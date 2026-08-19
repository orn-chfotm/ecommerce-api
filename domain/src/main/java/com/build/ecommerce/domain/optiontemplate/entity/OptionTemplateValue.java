package com.build.ecommerce.domain.optiontemplate.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "OPTION_TEMPLATE_VALUE",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_option_template_value_sort_order", columnNames = {"OPTION_TEMPLATE_ID", "SORT_ORDER"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "옵션 템플릿 값 테이블", on = "TABLE")
public class OptionTemplateValue extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_TEMPLATE_VALUE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPTION_TEMPLATE_ID", nullable = false)
    @Comment("옵션 템플릿 FK")
    private OptionTemplate optionTemplate;

    @Column(name = "OPTION_VALUE", nullable = false, length = 100)
    @Comment("옵션 값, not null (예: 블랙, M)")
    private String value;

    @Column(name = "SORT_ORDER", nullable = false)
    @Comment("정렬 순서")
    private int sortOrder;

    @Builder
    public OptionTemplateValue(String value, int sortOrder) {
        this.value = value;
        this.sortOrder = sortOrder;
    }

    void setOptionTemplate(OptionTemplate optionTemplate) {
        this.optionTemplate = optionTemplate;
    }

    public void changeValue(String value) {
        this.value = value;
    }

    public void changeSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
