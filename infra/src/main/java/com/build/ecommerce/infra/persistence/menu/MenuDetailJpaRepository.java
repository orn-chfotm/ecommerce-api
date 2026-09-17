package com.build.ecommerce.infra.persistence.menu;

import com.build.ecommerce.domain.menu.entity.MenuDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuDetailJpaRepository extends JpaRepository<MenuDetail, Long> {
    Optional<MenuDetail> findByIdAndMenuGroup_Id(Long menuDetailId, Long menuGroupId);

    boolean existsByParentId(Long parentId);

    boolean existsByMenuGroup_Id(Long menuGroupId);
}
