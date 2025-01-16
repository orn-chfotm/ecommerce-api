package com.build.ecommerce.core.security.auth.service.detail;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomDetails extends UserDetails {

    Long getId();
}