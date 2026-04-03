package com.build.ecommerce.domain.address.service;

import com.build.ecommerce.domain.address.dto.request.AddressRequest;
import com.build.ecommerce.domain.address.dto.response.AddressInfoResponse;
import com.build.ecommerce.domain.address.dto.response.AddressResponse;
import com.build.ecommerce.domain.address.entity.Address;
import com.build.ecommerce.domain.user.entity.User;
import com.build.ecommerce.domain.user.exception.UserNotFountException;
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

    @Transactional(readOnly = true)
    public AddressResponse getAddressList(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFountException::new);

        List<AddressInfoResponse> addressEntityResponse = user.getAddressList().stream()
                .map(AddressInfoResponse::toDto)
                .toList();

        return AddressResponse.toDto(addressEntityResponse);
    }

    public void registAddress(String name, AddressRequest request) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(UserNotFountException::new);

        Address address = Address.builder()
                .addressInfo(AddressRequest.toEntity(request))
                .build();

        user.getAddressList().add(address);
    }
}
