package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.exceptions.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

public class GlobalExceptionHandlerTest {

    @Test
    public void testHandleJwtExpiredTokenException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired"),
                ge.handleJwtExpiredTokenException(new JwtExpiredTokenException("expired"), null));
    }

    @SneakyThrows
    @Test
    public void testHandleAccessDeniedException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user"),
                ge.handleAccessDeniedException(new AccessDeniedException("access denied"), null));
    }

    @Test
    public void testHandleApplicationException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Auth Method Not supported"),
                ge.handleApplicationException(new AuthMethodNotSupportedException("Auth no supported"), null));

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request"),
                ge.handleApplicationException(new BadRequest("bad request"), null));

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.CONFLICT).body("entity already exist"),
                ge.handleApplicationException(new EntityAlreadyExistException("entity already exist"), null));
    }

    @Test
    public void testHandleInvalidJwtTokenException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token"),
                ge.handleInvalidJwtTokenException(new InvalidJwtToken(), null));
    }

    @Test
    public void testHandleUnAuthorizedAccessException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.FORBIDDEN).body("access denied"),
                ge.handleUnAuthorizedAccessException(new UnAuthorizedAccessException("access denied"), null));
    }

    @Test
    public void testHandleUncatchException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process request"),
                ge.handleUncatchException(new Exception("unknown exception"), null));
    }

    @Test
    public void testHandleEntityNotFoundException() {
        GlobalExceptionHandler ge = new GlobalExceptionHandler();
        Assertions.assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found"),
                ge.handleEntityNotFoundException(new EntityNotFoundException("not found"), null));
    }
}
