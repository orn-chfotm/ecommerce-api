package com.build.ecommerce.core.config.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtProperty {

    @Value("${service.jwt.token-type}")
    private String tokenType;
    @Value("${service.jwt.key}")
    private String key;
    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;
    @Value("${service.jwt.refresh-expiration}")
    private Long refreshExpiration;
}
