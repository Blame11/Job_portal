package com.jobportal.apigateway.constants;

public class GatewayConstants {
    public static final String HEADER_USER_ID = "X-USER-ID";
    public static final String HEADER_USER_ROLE = "X-USER-ROLE";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String COOKIE_NAME = "jobPortalToken";
    public static final String BEARER_PREFIX = "Bearer ";
    
    public static final String[] PUBLIC_PATHS = {
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/jobs",
        "/api/v1/health"
    };
}
