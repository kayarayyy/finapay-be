package com.bcaf.bcapay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.bcaf.bcapay.dto.ResponseDto;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handle Unauthorized Access (401)
        @ExceptionHandler(AuthenticationException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ResponseEntity<ResponseDto<Object>> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ResponseDto(401, "failed", ex.getMessage(), null));
        }

        // Handle Resource Not Found (404)
        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<ResponseDto<Object>> handleResourceNotFound(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseDto(404, "failed", ex.getMessage(), null));
        }

        // Handle Bad Request (400)
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ResponseDto<Object>> handleBadRequest(IllegalArgumentException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ResponseDto(400, "failed", ex.getMessage(), null));
        }

        // Handle Internal Server Error (500)

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ResponseDto<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ResponseDto(500, "failed", ex.getMessage(), null));
        }

        @ExceptionHandler(AccessDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ResponseEntity<ResponseDto<Object>> handleForbiddenException(Exception ex, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ResponseDto(403, "failed", ex.getMessage(), null));
        }

}
