package com.build.ecommerce.infra.file.local;

import com.build.ecommerce.core.config.properties.FileUploadProperties;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.support.file.FileNameGenerator;
import com.build.ecommerce.core.support.file.FileStoragePort;
import com.build.ecommerce.core.support.file.FileStoreResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!prod")
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStoragePort {

    private final FileUploadProperties properties;

    @Override
    public List<FileStoreResult> store(List<MultipartFile> files) {
        String[] dateParts = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).split("/");
        Path uploadDir = Paths.get(properties.getRootPath(), dateParts);

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성에 실패했습니다.", e);
        }

        List<FileStoreResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String extension = FileNameGenerator.extractExtension(file.getOriginalFilename());
            validateExtension(extension);
            String storedFileName = FileNameGenerator.generate(extension);

            try {
                file.transferTo(uploadDir.resolve(storedFileName));
            } catch (IOException e) {
                throw new RuntimeException("파일 저장에 실패했습니다.", e);
            }

            results.add(new FileStoreResult(
                    file.getOriginalFilename(),
                    storedFileName,
                    extension,
                    file.getSize(),
                    "/files/" + String.join("/", dateParts) + "/" + storedFileName
            ));
        }
        return results;
    }

    @Override
    public void delete(List<String> accessUrls) {
        for (String url : accessUrls) {
            String relative = url.replaceFirst("^/files/", "");
            Path filePath = Paths.get(properties.getRootPath(), relative.split("/"));
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
    }

    private void validateExtension(String extension) {
        if (!properties.getAllowedExtensions().contains(extension)) {
            throw new InvalidInputException("허용되지 않는 파일 형식입니다: " + extension);
        }
    }
}
