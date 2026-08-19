package com.build.ecommerce.core.support.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStoragePort {
    List<FileStoreResult> store(List<MultipartFile> files);
    void delete(List<String> accessUrls);
}
