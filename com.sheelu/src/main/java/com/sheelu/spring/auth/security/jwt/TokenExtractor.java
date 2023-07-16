package com.sheelu.spring.auth.security.jwt;

public interface TokenExtractor {
    String extract(String payload);
}
