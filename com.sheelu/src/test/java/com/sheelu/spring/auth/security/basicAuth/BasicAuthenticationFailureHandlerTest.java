package com.sheelu.spring.auth.security.basicAuth;

import com.sheelu.spring.auth.exceptions.AuthMethodNotSupportedException;
import com.sheelu.spring.auth.exceptions.JwtExpiredTokenException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;

class BasicAuthenticationFailureHandlerTest {

    @SneakyThrows
    @Test
    void onAuthenticationFailure() {
        BasicAuthenticationFailureHandler h = new BasicAuthenticationFailureHandler();

        //Case: BadCredentialsException
        HttpServletResponse res = new MockHttpServletResponse();
        h.onAuthenticationFailure(null, res, new BadCredentialsException("bad cred"));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), res.getStatus());

        //Case: JwtExpiredTokenException
        res = new MockHttpServletResponse();
        h.onAuthenticationFailure(null, res, new JwtExpiredTokenException("expired"));
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), res.getStatus());

        //Case: AuthMethodNotSupportedException
        res = new MockHttpServletResponse();
        h.onAuthenticationFailure(null, res, new AuthMethodNotSupportedException("Auth not supported"));
        Assertions.assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), res.getStatus());

        //Case: Unknown Exception
        res = new MockHttpServletResponse();
        h.onAuthenticationFailure(null, res, new AuthenticationException("unknown failure") {
            @Override
            public String getMessage() {
                return super.getMessage();
            }
        });
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), res.getStatus());
    }
}