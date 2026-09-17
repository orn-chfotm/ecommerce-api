package com.build.ecommerce.infra.persistence.menu;

import com.build.ecommerce.domain.menu.entity.MenuGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuGroupJpaRepository extends JpaRepository<MenuGroup, Long> {
}
