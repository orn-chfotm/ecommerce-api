package com.build.ecommerce.infra.persistence.optiontemplate;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;

import java.util.List;

interface OptionTemplateCustomRepository {

    List<OptionTemplate> findAllWithValues();
}
