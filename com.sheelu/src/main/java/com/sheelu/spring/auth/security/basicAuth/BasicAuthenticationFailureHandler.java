package com.sheelu.spring.auth.security.basicAuth;

import com.sheelu.spring.auth.exceptions.AuthMethodNotSupportedException;
import com.sheelu.spring.auth.exceptions.JwtExpiredTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BasicAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException {
		response.setContentType(MediaType.TEXT_PLAIN_VALUE);
		if (e instanceof BadCredentialsException) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Invalid credentials");
		} else if (e instanceof JwtExpiredTokenException) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Token has expired");
		} else if (e instanceof AuthMethodNotSupportedException) {
			response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
		    response.getWriter().write("Method Not Allowed");
		} else {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Authentication failed");
		}
	}
}
