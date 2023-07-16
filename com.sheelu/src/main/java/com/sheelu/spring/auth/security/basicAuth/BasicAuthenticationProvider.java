package com.sheelu.spring.auth.security.basicAuth;

import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.security.models.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {
    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public BasicAuthenticationProvider(final UserRepository userRepository, final BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        User user = userRepository
                .findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userName: " + userName));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (CollectionUtils.isEmpty(user.getRoles())) throw new InsufficientAuthenticationException("User has no roles assigned");

        List<GrantedAuthority> authorities =
                user.getRoles().stream().map(role -> role.getName().name()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserContext userContext = UserContext.create(user.getUserName(), authorities);
        
        return new UsernamePasswordAuthenticationToken(userContext, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
