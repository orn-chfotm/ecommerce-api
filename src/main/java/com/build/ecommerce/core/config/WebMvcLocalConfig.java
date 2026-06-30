package com.build.ecommerce.core.config;

import com.build.ecommerce.core.config.properties.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class WebMvcLocalConfig implements WebMvcConfigurer {

    private final FileUploadProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + properties.getRootPath() + "/");
    }
}
