package com.build.ecommerce.infra.persistence.cart;

interface CartCustomRepository {

    void deleteAllByUserId(Long userId);
}
