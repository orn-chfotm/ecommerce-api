package com.build.ecommerce.core.support.file;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileNameGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String generate(String extension) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "-" + uuid + "." + extension;
    }

    public static String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
