
package com.build.ecommerce.core.security.auth.service;

import com.build.ecommerce.core.security.auth.service.detail.impl.CustomUserDetails;
import com.build.ecommerce.domain.admin.exception.AdminNotFountException;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(AdminNotFountException::new);

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
