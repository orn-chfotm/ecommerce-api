package com.build.ecommerce.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    private String rootPath;
    private Map<FileUploadTarget, Integer> limits;
    private Set<String> allowedExtensions;

    public enum FileUploadTarget {
        PRODUCT
    }
}
