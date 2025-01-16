package com.build.ecommerce.domain.user.repository.impl;

import com.build.ecommerce.domain.user.dto.request.UserSearchRequest;
import com.build.ecommerce.domain.user.dto.response.UserResponse;
import com.build.ecommerce.domain.user.entity.Gender;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.repository.UserCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.build.ecommerce.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<List<UserResponse>> findUserList(UserSearchRequest request) {
        return Optional.ofNullable(
                queryFactory.select(getFields())
                        .from(user)
                        .where(allSearch(request))
                        .fetch()
        );
    }

    public Optional<UserResponse> findUserById(Long userId) {
        return Optional.ofNullable(
                queryFactory.select(getFields())
                        .from(user)
                        .where(userIdEq(userId))
                        .fetchOne()
        );
    }

    private QBean<UserResponse> getFields() {
        return Projections.fields(
                UserResponse.class,
                user.id,
                user.email,
                user.name,
                user.gender,
                user.birthDate
        );
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : Expressions.FALSE;
    }

    private BooleanExpression emailLike(String emailParam) {
        return emailParam != null ? user.email.contains(emailParam) : null;
    }

    private BooleanExpression nameLike(String nameParam) {
        return nameParam != null ? user.name.contains(nameParam) : null;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? user.gender.eq(gender) : null;
    }

    private BooleanExpression allSearch(UserSearchRequest request) {
        return emailLike(request.email())
                .and(nameLike(request.name()))
                .and(genderEq(Gender.valueOf(request.gender())));
    }
}
