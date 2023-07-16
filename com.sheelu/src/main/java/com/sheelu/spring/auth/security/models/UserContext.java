package com.sheelu.spring.auth.security.models;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class UserContext {
    private final String userId;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.userId = username;
        this.authorities = authorities;
    }
    
    public static UserContext create(String userId, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(userId)) throw new IllegalArgumentException("UserId is blank: " + userId);
        return new UserContext(userId, authorities);
    }

    public String getUserId() {
        return userId;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
