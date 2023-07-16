package com.sheelu.spring.auth.security.basicAuth;

import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.models.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

class BasicAuthenticationProviderTest {

    @InjectMocks
    BasicAuthenticationProvider basicAuthenticationProvider;

    @Spy
    private BCryptPasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAuthenticate() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("u1", "password");

        //Case: User doesn't exist
        Mockito.when(userRepository.findByUserName("u1")).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            basicAuthenticationProvider.authenticate(authentication);
        });

        //Case: password doesn't match
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder()
                        .userName("u1")
                        .password(new BCryptPasswordEncoder().encode("wrong_password"))
                        .build()));
        Assertions.assertThrows(BadCredentialsException.class, () -> {
            basicAuthenticationProvider.authenticate(authentication);
        });

        //Case: No Role assigned to user
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder()
                        .userName("u1")
                        .password(new BCryptPasswordEncoder().encode("password"))
                        .roles(new HashSet<>())
                        .build()));
        Assertions.assertThrows(InsufficientAuthenticationException.class, () -> {
            basicAuthenticationProvider.authenticate(authentication);
        });

        //Case: Authenticate successfully
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder()
                        .userName("u1")
                        .password(new BCryptPasswordEncoder().encode("password"))
                        .roles(new HashSet<Role>(Arrays.asList(new Role(1, UserRole.ADMIN))))
                        .build()));

        Assertions.assertDoesNotThrow(() -> {
            basicAuthenticationProvider.authenticate(authentication);
        });
    }

    @Test
    public void testSupport() {
        Assertions.assertEquals(false, basicAuthenticationProvider.supports(Object.class));
        Assertions.assertEquals(true, basicAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }
}