package com.build.ecommerce.infra.persistence.admin;

import com.build.ecommerce.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String username);

    boolean existsByEmail(String email);
}
