package com.build.ecommerce.core.support.file;

public record FileStoreResult(
        String originalFileName,
        String storedFileName,
        String extension,
        Long fileSize,
        String accessUrl
) {}
