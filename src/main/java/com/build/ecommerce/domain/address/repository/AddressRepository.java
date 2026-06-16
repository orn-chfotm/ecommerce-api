package com.build.ecommerce.domain.address.repository;

import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

    Long user(User user);
}
