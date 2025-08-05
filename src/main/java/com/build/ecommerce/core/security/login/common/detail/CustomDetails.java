package com.build.ecommerce.core.security.login.common.detail;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomDetails extends UserDetails {

    Long getId();
}