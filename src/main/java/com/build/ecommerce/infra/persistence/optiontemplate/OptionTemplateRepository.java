package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OptionTemplateRepository extends JpaRepository<OptionTemplate, Long> {

    @Query("select distinct ot from OptionTemplate ot left join fetch ot.optionTemplateValues")
    List<OptionTemplate> findAllWithValues();
}
