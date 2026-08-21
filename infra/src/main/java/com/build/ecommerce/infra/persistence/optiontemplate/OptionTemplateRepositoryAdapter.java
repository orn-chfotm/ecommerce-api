package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import com.build.ecommerce.domain.optiontemplate.repository.OptionTemplateRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.optiontemplate.entity.QOptionTemplate.optionTemplate;
import static com.build.ecommerce.domain.optiontemplate.entity.QOptionTemplateValue.optionTemplateValue;

@Repository
@RequiredArgsConstructor
class OptionTemplateRepositoryAdapter implements OptionTemplateRepository {

    private final OptionTemplateJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public OptionTemplate save(OptionTemplate optionTemplateEntity) {
        return jpaRepository.save(optionTemplateEntity);
    }

    @Override
    public Optional<OptionTemplate> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<OptionTemplate> findAllWithValues() {
        return jpaQueryFactory.selectFrom(optionTemplate)
                .distinct()
                .leftJoin(optionTemplate.optionTemplateValues, optionTemplateValue).fetchJoin()
                .fetch();
    }

    @Override
    public void delete(OptionTemplate optionTemplateEntity) {
        jpaRepository.delete(optionTemplateEntity);
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }
}
