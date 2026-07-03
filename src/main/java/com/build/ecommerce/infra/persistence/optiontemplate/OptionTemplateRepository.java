package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionTemplateRepository extends JpaRepository<OptionTemplate, Long>, OptionTemplateCustomRepository {
}
