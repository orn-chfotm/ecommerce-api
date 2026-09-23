package com.build.ecommerce.core.security.exception.extend;


import com.build.ecommerce.core.security.exception.SecurityAuthenticationException;

import static com.build.ecommerce.core.exception.code.ExceptionCode.AUTHENTICATION_UNAUTHORIZED;

public class AuthorityNotFoundException extends SecurityAuthenticationException {
    public AuthorityNotFoundException() {
        super(AUTHENTICATION_UNAUTHORIZED);
    }
}
