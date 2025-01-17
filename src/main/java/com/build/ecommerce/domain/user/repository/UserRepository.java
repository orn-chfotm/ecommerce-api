package com.build.ecommerce.domain.user.repository;

import com.build.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository{

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
