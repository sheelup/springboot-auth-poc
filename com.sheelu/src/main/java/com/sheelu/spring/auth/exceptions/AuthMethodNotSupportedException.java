package com.sheelu.spring.auth.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 370502342355345241L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
