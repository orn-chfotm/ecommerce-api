package com.build.ecommerce.domain.user.repository;

import com.build.ecommerce.domain.user.dto.request.UserSearchRequest;
import com.build.ecommerce.domain.user.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    Optional<List<UserResponse>> findUserList(UserSearchRequest request);

    Optional<UserResponse> findUserById(Long userId);
}
