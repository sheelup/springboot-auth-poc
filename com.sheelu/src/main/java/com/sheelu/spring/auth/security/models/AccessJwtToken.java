package com.sheelu.spring.auth.security.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.Claims;
import lombok.Getter;

public final class AccessJwtToken implements JwtToken {
    private final String token;
    @JsonIgnore

    @Getter
    private Claims claims;

    protected AccessJwtToken(final String token, Claims claims) {
        this.token = token;
        this.claims = claims;
    }

    public String getToken() {
        return this.token;
    }
}
