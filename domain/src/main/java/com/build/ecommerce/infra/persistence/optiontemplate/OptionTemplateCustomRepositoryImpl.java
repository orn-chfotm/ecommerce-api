package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.build.ecommerce.domain.optiontemplate.entity.QOptionTemplate.optionTemplate;
import static com.build.ecommerce.domain.optiontemplate.entity.QOptionTemplateValue.optionTemplateValue;

@RequiredArgsConstructor
class OptionTemplateCustomRepositoryImpl implements OptionTemplateCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OptionTemplate> findAllWithValues() {
        return jpaQueryFactory.selectFrom(optionTemplate)
                .distinct()
                .leftJoin(optionTemplate.optionTemplateValues, optionTemplateValue).fetchJoin()
                .fetch();
    }
}
