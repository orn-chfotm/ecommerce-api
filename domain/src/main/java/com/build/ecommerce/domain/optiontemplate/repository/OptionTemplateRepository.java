package com.build.ecommerce.domain.optiontemplate.repository;

import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;

import java.util.List;
import java.util.Optional;

public interface OptionTemplateRepository {

    OptionTemplate save(OptionTemplate optionTemplate);

    Optional<OptionTemplate> findById(Long id);

    List<OptionTemplate> findAllWithValues();

    void delete(OptionTemplate optionTemplate);

    void flush();
}
