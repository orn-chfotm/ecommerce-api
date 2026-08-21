package com.build.ecommerce.infra.persistence.address;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class AddressRepositoryAdapter implements AddressRepository {

    private final AddressJpaRepository jpaRepository;

    @Override
    public Address saveAndFlush(Address address) {
        return jpaRepository.saveAndFlush(address);
    }

    @Override
    public Optional<Address> findByIdAndUserId(Long addressId, Long userId) {
        return jpaRepository.findByIdAndUserId(addressId, userId);
    }
}
