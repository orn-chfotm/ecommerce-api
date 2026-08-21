package com.build.ecommerce.infra.persistence.admin;

import com.build.ecommerce.domain.admin.entity.Admin;
import com.build.ecommerce.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class AdminRepositoryAdapter implements AdminRepository {

    private final AdminJpaRepository jpaRepository;

    @Override
    public Admin save(Admin admin) {
        return jpaRepository.save(admin);
    }

    @Override
    public Optional<Admin> findByEmail(String username) {
        return jpaRepository.findByEmail(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
