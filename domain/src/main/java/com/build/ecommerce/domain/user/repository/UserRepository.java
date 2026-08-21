package com.build.ecommerce.domain.user.repository;

import com.build.ecommerce.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
