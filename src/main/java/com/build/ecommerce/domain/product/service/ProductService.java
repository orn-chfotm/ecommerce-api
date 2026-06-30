package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.core.config.properties.FileUploadProperties;
import com.build.ecommerce.core.support.file.FileStoragePort;
import com.build.ecommerce.core.support.file.FileStoreResult;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.domain.product.exception.FileUploadExceedLimitException;
import com.build.ecommerce.domain.product.exception.ProductNotFoundException;
import com.build.ecommerce.infra.file.entity.FileDetail;
import com.build.ecommerce.infra.file.entity.FileMaster;
import com.build.ecommerce.infra.file.enums.FileMasterType;
import com.build.ecommerce.infra.persistence.file.FileMasterRepository;
import com.build.ecommerce.infra.persistence.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.build.ecommerce.core.config.properties.FileUploadProperties.FileUploadTarget.PRODUCT;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileMasterRepository fileMasterRepository;
    private final FileStoragePort fileStoragePort;
    private final FileUploadProperties fileUploadProperties;

    public ProductResponse insertProduct(ProductRequest request) {
        List<MultipartFile> files = request.files();
        validateFileCount(files);

        Product product = request.toEntity();
        productRepository.save(product);

        if (files != null && !files.isEmpty()) {
            List<FileStoreResult> storeResults = fileStoragePort.store(files);
            registerRollbackCleanup(storeResults);

            FileMaster fileMaster = FileMaster.builder()
                    .referenceType(FileMasterType.PRODUCT)
                    .build();
            for (int i = 0; i < storeResults.size(); i++) {
                FileStoreResult result = storeResults.get(i);
                fileMaster.addFileDetail(FileDetail.builder()
                        .fileMaster(fileMaster)
                        .sortOrder(i)
                        .storedFileName(result.storedFileName())
                        .originalFileName(result.originalFileName())
                        .extension(result.extension())
                        .fileSize(result.fileSize())
                        .path(result.accessUrl())
                        .build());
            }

            fileMasterRepository.save(fileMaster);
            product.attachFiles(fileMaster);
        }

        return ProductResponse.toCreateDto(product.getId());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductDetail(final Long productId) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return ProductResponse.toDto(findProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductList(ProductSearchRequest searchRequest, Pageable pageable) {
        return productRepository.searchProducts(searchRequest, pageable)
                .map(ProductResponse::toDto);
    }

    public ProductResponse deleteProduct(final Long productId) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        findProduct.markDelete();
        return ProductResponse.toDto(findProduct);
    }

    private void validateFileCount(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;
        int maxCount = fileUploadProperties.getLimits().get(PRODUCT);
        if (files.size() > maxCount) {
            throw new FileUploadExceedLimitException();
        }
    }

    private void registerRollbackCleanup(List<FileStoreResult> storeResults) {
        List<String> accessUrls = storeResults.stream()
                .map(FileStoreResult::accessUrl)
                .toList();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    fileStoragePort.delete(accessUrls);
                }
            }
        });
    }
}
