package com.build.ecommerce.domain.product.service;

import com.build.ecommerce.core.config.properties.FileUploadProperties;
import com.build.ecommerce.core.support.file.FileStoragePort;
import com.build.ecommerce.core.support.file.FileStoreResult;
import com.build.ecommerce.domain.product.dto.request.ProductRequest;
import com.build.ecommerce.domain.product.dto.request.ProductSearchRequest;
import com.build.ecommerce.domain.product.dto.request.ProductUpdateRequest;
import com.build.ecommerce.domain.product.dto.response.FileDetailResponse;
import com.build.ecommerce.domain.product.dto.response.ProductResponse;
import com.build.ecommerce.domain.product.enums.ProductSearchScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.build.ecommerce.domain.product.entity.Product;
import com.build.ecommerce.core.exception.type.InvalidInputException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.file.entity.FileDetail;
import com.build.ecommerce.domain.file.entity.FileMaster;
import com.build.ecommerce.domain.file.enums.FileMasterType;
import com.build.ecommerce.domain.file.repository.FileMasterRepository;
import com.build.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return ProductResponse.toDto(findProductById(productId));
    }

    /**
     * 사용자 노출용 상세 조회. 추가구성상품(ADD_ON)은 단독 노출 대상이 아니므로 존재 자체를 숨긴다.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductDetailForUser(final Long productId) {
        Product findProduct = findProductById(productId);

        // 노출 게이트를 통과하지 못한 상품(삭제/비노출/노출 시작 전)도 404로 처리한다.
        // 403/409로 구분해 응답하면 "그 ID의 상품이 존재한다"는 사실이 드러나므로 존재 자체를 숨긴다.
        if (findProduct.isAddOn() || !findProduct.isVisibleToUser()) {
            throw new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND);
        }

        return ProductResponse.toDto(findProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductList(ProductSearchRequest searchRequest, Pageable pageable) {
        // 관리자용: 상품 유형/노출 필터 없이 전체 조회한다.
        return searchProducts(searchRequest, ProductSearchScope.ADMIN, pageable);
    }

    /**
     * 사용자 노출용 목록 조회. 클라이언트 입력과 무관하게 서버에서 사용자 조회 범위를 강제한다.
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductListForUser(ProductSearchRequest searchRequest, Pageable pageable) {
        return searchProducts(searchRequest, ProductSearchScope.USER, pageable);
    }

    private Product findProductById(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));
    }

    private Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest, ProductSearchScope scope, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(searchRequest, scope, pageable);

        List<Long> fileMasterIds = productPage.getContent().stream()
                .map(Product::getFileMaster)
                .filter(fm -> fm != null)
                .map(FileMaster::getId)
                .distinct()
                .toList();

        Map<Long, List<FileDetailResponse>> filesByFileMasterId = fileMasterIds.isEmpty()
                ? Map.of()
                : fileMasterRepository.findAllWithDetailsByIdIn(fileMasterIds).stream()
                        .collect(Collectors.toMap(FileMaster::getId, fm -> fm.getFileDetailList().stream()
                                .map(FileDetailResponse::toDto)
                                .toList()));

        return productPage.map(product -> {
            FileMaster fileMaster = product.getFileMaster();
            List<FileDetailResponse> files = fileMaster == null ? null : filesByFileMasterId.get(fileMaster.getId());
            return ProductResponse.toDto(product, files);
        });
    }

    public ProductResponse updateProductDetail(Long productId, ProductUpdateRequest request) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));
        findProduct.update(request);
        return ProductResponse.toDto(findProduct);
    }

    public ProductResponse deleteProduct(final Long productId) {
        Product findProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ProductExceptionCode.PRODUCT_NOT_FOUND));
        findProduct.markDelete();
        return ProductResponse.toDto(findProduct);
    }

    private void validateFileCount(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;
        int maxCount = fileUploadProperties.getLimits().get(PRODUCT);
        if (files.size() > maxCount) {
            throw new InvalidInputException(ProductExceptionCode.FILE_UPLOAD_EXCEED_LIMIT);
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
