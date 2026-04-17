package com.build.ecommerce.domain.address.service;

import com.build.ecommerce.domain.address.dto.request.AddressRequest;
import com.build.ecommerce.domain.address.dto.response.AddressInfoResponse;
import com.build.ecommerce.domain.address.dto.response.AddressResponse;
import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.address.repository.AddressRepository;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFoundException;
import com.build.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public AddressResponse getAddressList(final Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<AddressInfoResponse> addressEntityResponse = user.getAddressList().stream()
                .map(AddressInfoResponse::toDto)
                .toList();

        return AddressResponse.toDto(addressEntityResponse);
    }

    public AddressInfoResponse registerAddress(final Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Address address = Address.builder()
                .addressInfo(AddressRequest.toEntity(request))
                .build();
        user.addAddress(address);
        Address savedAddress = addressRepository.saveAndFlush(address);

        return AddressInfoResponse.toDto(savedAddress);
    }
}
