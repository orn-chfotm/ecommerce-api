package com.build.ecommerce.infra.persistence.address;

import com.build.ecommerce.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface AddressJpaRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
}
