package com.build.ecommerce.domain.product.entity;

import com.build.ecommerce.core.persistence.BaseTimeEntity;
import com.build.ecommerce.domain.product.dto.request.ProductUpdateRequest;
import com.build.ecommerce.domain.product.enums.ProductCategoryType;
import com.build.ecommerce.domain.product.enums.ProductStatusType;
import com.build.ecommerce.domain.product.enums.ProductType;
import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.domain.product.exception.code.ProductExceptionCode;
import com.build.ecommerce.domain.file.entity.FileMaster;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "PRODUCTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment(value = "product information table", on = "TABLE")
@Getter
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("제품 카테고리, not null")
    private ProductCategoryType category;

    @Column(nullable = false, length = 200)
    @Comment("제품 명, not null")
    private String name;

    @Column(length = 2000)
    @Comment("제품 설명")
    private String description;

    @Column(nullable = false)
    @Comment("제품 가격, not null")
    private BigDecimal price;

    @Comment("제품 수량, (null 은 미지정 / 0 은 재고 없음 의미)")
    private Integer stockQuantity;

    @Comment("제품 최소 주문 수량, default 1")
    private int minOrderQuantity = 1;

    @Comment("제품 노출 여부, default false")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Comment("제품 상태 값")
    private ProductStatusType status;

    @Comment("제품 상태 값")
    private LocalDateTime delAt;

    @Comment("노출 시작 시점, null이면 즉시 노출")
    private LocalDateTime displayStartAt;

    @Column(nullable = false)
    @Comment("옵션 등록 여부, default false (true 면 옵션 조합별 재고 사용, false 면 stockQuantity 사용)")
    private boolean hasOptions;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUCT_TYPE", nullable = false, length = 20)
    @Comment("상품 유형 (NORMAL=일반, ADD_ON=추가구성상품), default NORMAL")
    private ProductType productType = ProductType.NORMAL;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    @Comment("파일 마스터 FK")
    private FileMaster fileMaster;

    @OneToMany(mappedBy = "product")
    @Comment("찜 리스트")
    private List<ProductWish> productWish = new ArrayList<>();

    @Builder
    public Product(ProductCategoryType category, String name, String description, BigDecimal price, Integer stockQuantity, int minOrderQuantity, boolean active, ProductType productType, LocalDateTime displayStartAt) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minOrderQuantity = minOrderQuantity;
        this.active = active;
        // 미지정 시 일반 상품으로 처리한다.
        this.productType = productType == null ? ProductType.NORMAL : productType;
        this.displayStartAt = displayStartAt;
    }

    public boolean isAddOn() {
        return productType == ProductType.ADD_ON;
    }

    // 아래 판정 메서드 3개와 동일한 판정이 ProductRepositoryAdapter의 QueryDSL(visibleToUser)에도 존재한다.
    // 노출/주문 가능 조건을 변경할 때는 반드시 양쪽을 함께 수정해야 한다.
    public boolean isDeleted() {
        return delAt != null;
    }

    public boolean isVisibleToUser() {
        return !isDeleted()
                && active
                && (displayStartAt == null || !displayStartAt.isAfter(LocalDateTime.now()));
    }

    public boolean isOrderable() {
        // status는 null 허용 값이며, null은 "상태 미지정 = 주문 가능"을 의미한다.
        return isVisibleToUser()
                && (status == null || status == ProductStatusType.SELLING);
    }

    public void addStock(int quantity) {
        if (stockQuantity == null) return;
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (stockQuantity == null) {
            return;
        }
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new BusinessException(ProductExceptionCode.PRODUCT_NOT_ENOUGH_STOCK);
        }
        this.stockQuantity = restStock;
    }

    public void changeStatus(ProductStatusType statusType) {
        this.status = statusType;
    }

    public void markDelete() {
        this.status = ProductStatusType.DELETED;
        this.delAt = LocalDateTime.now();
        // 삭제된 상품은 노출 대상에서도 제외한다.
        this.active = false;
    }

    public void attachFiles(FileMaster fileMaster) {
        this.fileMaster = fileMaster;
    }

    public void markOptionsRegistered() {
        this.hasOptions = true;
    }

    public void update(ProductUpdateRequest request) {
        this.category = request.category();
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.stockQuantity = request.stockQuantity();
        this.minOrderQuantity = request.minOrderQuantity();
        this.active = request.active();
        this.displayStartAt = request.displayStartAt();
        // 미지정 시 기존 유형을 유지한다.
        // (등록과 달리 수정에서 NORMAL로 덮어쓰면, 이미 다른 상품에 매핑된 ADD_ON 상품이
        //  무관한 필드 수정만으로 일반 상품이 되어버린다.)
        if (request.productType() != null) {
            this.productType = request.productType();
        }
    }
}
