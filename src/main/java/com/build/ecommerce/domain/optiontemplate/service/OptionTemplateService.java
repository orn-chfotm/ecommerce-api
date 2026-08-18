package com.build.ecommerce.domain.optiontemplate.service;

import com.build.ecommerce.domain.optiontemplate.dto.request.OptionTemplateRequest;
import com.build.ecommerce.domain.optiontemplate.dto.response.OptionTemplateResponse;
import com.build.ecommerce.domain.optiontemplate.entity.OptionTemplate;
import com.build.ecommerce.domain.optiontemplate.exception.OptionTemplateNotFoundException;
import com.build.ecommerce.infra.persistence.optiontemplate.OptionTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionTemplateService {

    private final OptionTemplateRepository optionTemplateRepository;

    public OptionTemplateResponse insertOptionTemplate(OptionTemplateRequest request) {
        OptionTemplate optionTemplate = request.toEntity();
        optionTemplateRepository.save(optionTemplate);

        return OptionTemplateResponse.toCreateDto(optionTemplate.getId());
    }

    @Transactional(readOnly = true)
    public List<OptionTemplateResponse> getOptionTemplateList() {
        return optionTemplateRepository.findAllWithValues().stream()
                .map(OptionTemplateResponse::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OptionTemplateResponse getOptionTemplateDetail(final Long optionTemplateId) {
        OptionTemplate findOptionTemplate = optionTemplateRepository.findById(optionTemplateId)
                .orElseThrow(OptionTemplateNotFoundException::new);

        return OptionTemplateResponse.toDto(findOptionTemplate);
    }

    public OptionTemplateResponse updateOptionTemplate(final Long optionTemplateId, OptionTemplateRequest request) {
        OptionTemplate findOptionTemplate = optionTemplateRepository.findById(optionTemplateId)
                .orElseThrow(OptionTemplateNotFoundException::new);

        findOptionTemplate.changeName(request.name());
        findOptionTemplate.clearOptionTemplateValues();
        optionTemplateRepository.flush();
        request.optionTemplateValues().forEach(valueRequest ->
                findOptionTemplate.addOptionTemplateValue(valueRequest.toEntity()));

        return OptionTemplateResponse.toDto(findOptionTemplate);
    }

    public OptionTemplateResponse deleteOptionTemplate(final Long optionTemplateId) {
        OptionTemplate findOptionTemplate = optionTemplateRepository.findById(optionTemplateId)
                .orElseThrow(OptionTemplateNotFoundException::new);

        OptionTemplateResponse response = OptionTemplateResponse.toDto(findOptionTemplate);
        optionTemplateRepository.delete(findOptionTemplate);

        return response;
    }
}
