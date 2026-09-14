package com.build.ecommerce.domain.user.service;

import com.build.ecommerce.core.exception.type.BusinessException;
import com.build.ecommerce.core.exception.type.NotFoundException;
import com.build.ecommerce.domain.user.dto.request.UserRequest;
import com.build.ecommerce.domain.user.dto.response.UserResponse;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.code.UserExceptionCode;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUserDetail(final Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserExceptionCode.USER_NOT_FOUND));
        return UserResponse.toDto(findUser);
    }

    public UserResponse registerUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new BusinessException(UserExceptionCode.USER_ALREADY_EXISTS);
        }

        User user = UserRequest.toEntity(userRequest, passwordEncoder);
        userRepository.save(user);
        return UserResponse.toDto(user);
    }
}
