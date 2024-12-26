package com.build.ecommerce.domain.user.service;

import com.build.ecommerce.core.util.LocalDateUtil;
import com.build.ecommerce.domain.user.dto.request.UserRequest;
import com.build.ecommerce.domain.user.entity.Gender;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRequest userRequest) {
        userRepository.save(User.builder()
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .name(userRequest.name())
                .gender(Gender.valueOf(userRequest.gender()))
                .birthDate(LocalDateUtil.toLocalDate(userRequest.birthDate()))
            .build());
    }
}