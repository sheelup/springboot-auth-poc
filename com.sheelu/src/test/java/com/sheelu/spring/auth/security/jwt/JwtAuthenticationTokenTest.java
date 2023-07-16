package com.sheelu.spring.auth.security.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;


class JwtAuthenticationTokenTest {

    @Test
    void testSetAuthenticated() {
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            authToken.setAuthenticated(true);
        });

        JwtAuthenticationToken authToken1 = new JwtAuthenticationToken(null, Collections.emptyList());
        Assertions.assertDoesNotThrow(() -> {
            authToken1.setAuthenticated(false);
        });
    }
}