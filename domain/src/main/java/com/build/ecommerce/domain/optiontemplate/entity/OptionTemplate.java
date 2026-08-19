package com.build.ecommerce.domain.optiontemplate.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OPTION_TEMPLATE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Comment(value = "옵션 템플릿 테이블", on = "TABLE")
public class OptionTemplate extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPTION_TEMPLATE_ID")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("옵션 템플릿 명, not null (예: 사이즈, 색상)")
    private String name;

    @OneToMany(mappedBy = "optionTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Comment("옵션 템플릿 값 리스트")
    private List<OptionTemplateValue> optionTemplateValues = new ArrayList<>();

    @Builder
    public OptionTemplate(String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void addOptionTemplateValue(OptionTemplateValue optionTemplateValue) {
        this.optionTemplateValues.add(optionTemplateValue);
        optionTemplateValue.setOptionTemplate(this);
    }

    public void clearOptionTemplateValues() {
        this.optionTemplateValues.clear();
    }
}
