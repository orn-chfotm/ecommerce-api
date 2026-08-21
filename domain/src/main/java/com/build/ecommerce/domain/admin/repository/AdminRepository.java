package com.build.ecommerce.domain.admin.repository;

import com.build.ecommerce.domain.admin.entity.Admin;

import java.util.Optional;

public interface AdminRepository {

    Admin save(Admin admin);

    Optional<Admin> findByEmail(String username);

    boolean existsByEmail(String email);
}
