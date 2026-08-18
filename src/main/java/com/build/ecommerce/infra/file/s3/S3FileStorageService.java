package com.build.ecommerce.infra.file.s3;

import com.build.ecommerce.core.config.properties.FileUploadProperties;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.support.file.FileNameGenerator;
import com.build.ecommerce.core.support.file.FileStoragePort;
import com.build.ecommerce.core.support.file.FileStoreResult;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3FileStorageService implements FileStoragePort {

    private final S3Template s3Template;
    private final FileUploadProperties properties;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public List<FileStoreResult> store(List<MultipartFile> files) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        List<FileStoreResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            String extension = FileNameGenerator.extractExtension(file.getOriginalFilename());
            validateExtension(extension);
            String storedFileName = FileNameGenerator.generate(extension);
            String s3Key = "product/" + datePath + "/" + storedFileName;

            try {
                s3Template.upload(bucket, s3Key, file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
            }

            results.add(new FileStoreResult(
                    file.getOriginalFilename(),
                    storedFileName,
                    extension,
                    file.getSize(),
                    "https://" + bucket + ".s3." + region + ".amazonaws.com/" + s3Key
            ));
        }
        return results;
    }

    @Override
    public void delete(List<String> accessUrls) {
        for (String url : accessUrls) {
            String key = url.substring(url.indexOf("amazonaws.com/") + "amazonaws.com/".length());
            s3Template.deleteObject(bucket, key);
        }
    }

    private void validateExtension(String extension) {
        if (!properties.getAllowedExtensions().contains(extension)) {
            throw new InvalidInputException("허용되지 않는 파일 형식입니다: " + extension);
        }
    }
}
