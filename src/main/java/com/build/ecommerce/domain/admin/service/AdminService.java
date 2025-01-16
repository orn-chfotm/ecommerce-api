package com.build.ecommerce.domain.admin.service;

import com.build.ecommerce.domain.admin.dto.request.AdminRequest;
import com.build.ecommerce.domain.admin.dto.response.AdminResponse;
import com.build.ecommerce.domain.admin.entity.Admin;
import com.build.ecommerce.domain.admin.exception.AdminExistException;
import com.build.ecommerce.domain.admin.exception.AdminNotFountException;
import com.build.ecommerce.domain.admin.repository.AdminRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AdminResponse getAdminDetail(AdminRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(AdminNotFountException::new);

        return AdminResponse.toDto(admin);
    }

    public AdminResponse registerAdmin(AdminRequest request) {
        /* email 검증 우선 실시 */
        if (adminRepository.existsByEmail(request.email())) {
            throw new AdminExistException();
        }

        Admin admin = AdminRequest.toEntity(request, passwordEncoder);
        adminRepository.save(admin);

        return AdminResponse.toDto(admin);
    }
}
