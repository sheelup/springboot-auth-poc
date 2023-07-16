package com.sheelu.spring.auth.util;

import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.security.models.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextUtils {
    public boolean isAdminUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .filter(role -> UserRole.ADMIN.name().equals(role.getAuthority()))
                .findAny().isPresent();
    }

    public String loggedInUserName() {
        return ((UserContext)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
    }
}
