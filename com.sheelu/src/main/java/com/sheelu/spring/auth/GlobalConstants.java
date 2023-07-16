package com.sheelu.spring.auth;

public class GlobalConstants {
    public static final String SYSTEM_PUBLIC_API_PATTERN = "/system/public/**";
    public static final String ROOT_API_PATTERN = "/api/**";
    public static final String AUTH_API_PATTERN = "/auth/**";
    public static final String ADMIN_API_PATTERN = "/admin/**";
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

    public static final String HEALTH_API_V1_API = "/system/public/api/v1/health";
    public static final String AUTHENTICATION_URL = "/auth/api/v1/login";
    public static final String SIGNUP_URL = "/api/v1/users";
    public static final String REFRESH_TOKEN_URL = "/auth/api/v1/token";

}
