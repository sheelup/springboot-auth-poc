package com.sheelu.spring.auth.security.jwt;

public interface TokenVerifier {
    public boolean verify(String jti);
}
