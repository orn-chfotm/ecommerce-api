package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

interface OptionTemplateJpaRepository extends JpaRepository<OptionTemplate, Long> {
}
