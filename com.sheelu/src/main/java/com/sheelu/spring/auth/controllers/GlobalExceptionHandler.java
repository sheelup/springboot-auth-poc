package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->{

            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = JwtExpiredTokenException.class)
    public ResponseEntity<String> handleJwtExpiredTokenException(JwtExpiredTokenException ex, WebRequest request){
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
    }

    @ExceptionHandler(value = AccessDeniedException.class )
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex, WebRequest request ) throws IOException {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user");
    }

    @ExceptionHandler(value = AuthMethodNotSupportedException.class)
    public ResponseEntity<String> handleApplicationException(AuthMethodNotSupportedException ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Auth Method Not supported");
    }

    @ExceptionHandler(value = BadRequest.class)
    public ResponseEntity<String> handleApplicationException(BadRequest ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = EntityAlreadyExistException.class)
    public ResponseEntity<String> handleApplicationException(EntityAlreadyExistException ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = InvalidJwtToken.class)
    public ResponseEntity<String> handleInvalidJwtTokenException(InvalidJwtToken ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
    }

    @ExceptionHandler(value = UnAuthorizedAccessException.class)
    public ResponseEntity<String> handleUnAuthorizedAccessException(UnAuthorizedAccessException ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleUncatchException(Exception ex, WebRequest request) {
        logger.info("Failed to process request due to: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process request");
    }
}