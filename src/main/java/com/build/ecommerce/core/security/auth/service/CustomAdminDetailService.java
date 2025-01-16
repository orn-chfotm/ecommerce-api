
package com.build.ecommerce.core.security.auth.service;

import com.build.ecommerce.core.security.auth.service.detail.impl.CustomUserDetails;
import com.build.ecommerce.domain.admin.entity.Admin;
import com.build.ecommerce.domain.admin.exception.AdminNotFountException;
import com.build.ecommerce.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomAdminDetailService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(username)
                .orElseThrow(AdminNotFountException::new);

        return new CustomUserDetails(
                admin.getId(),
                admin.getEmail(),
                admin.getPassword(),
                Set.of(new SimpleGrantedAuthority(admin.getRole().name()))
        );
    }
}
