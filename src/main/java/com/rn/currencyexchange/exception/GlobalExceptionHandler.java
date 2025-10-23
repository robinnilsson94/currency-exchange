package com.rn.currencyexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException thrown in the application.
     *
     * @param ex the exception
     * @return a ResponseEntity containing error details with HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles RiksbankApiException thrown when there is an issue calling the Riksbank API.
     *
     * @param ex the exception
     * @return a ResponseEntity containing error details with HTTP 503 Service Unavailable
     */
    @ExceptionHandler(RiksbankApiException.class)
    public ResponseEntity<Map<String, String>> handleRiksbankApiException(RiksbankApiException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Riksbank API error: " + ex.getMessage());
    }

    /**
     * Handles HttpMessageNotReadableException, typically thrown when an invalid enum value is provided.
     *
     * @return a ResponseEntity containing error details with HTTP 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEnum() {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid currency provided");
    }

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("status", String.valueOf(status.value()));
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(response, status);
    }
}
