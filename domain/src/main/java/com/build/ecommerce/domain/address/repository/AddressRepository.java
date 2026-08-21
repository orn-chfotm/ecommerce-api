package com.build.ecommerce.domain.address.repository;

import com.build.ecommerce.domain.address.entity.Address;

import java.util.Optional;

public interface AddressRepository {

    Address saveAndFlush(Address address);

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
}
