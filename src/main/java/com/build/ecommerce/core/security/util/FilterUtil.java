package com.build.ecommerce.core.security.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class FilterUtil {

    private final static String LOGIN_REQUEST_HTTP_METHOD = HttpMethod.POST.name();
    private final static String LOGIN_REQUEST_CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE;

    public static RequestMatcher getRequestMatcher(final String loginRequestUrl) {
        return new AntPathRequestMatcher(loginRequestUrl, LOGIN_REQUEST_HTTP_METHOD);
    }

    public static boolean isApplicationJson(String contentType) {
        return contentType != null && contentType.equals(LOGIN_REQUEST_CONTENT_TYPE);
    }
}
