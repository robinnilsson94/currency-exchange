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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RiksbankApiException.class)
    public ResponseEntity<Map<String, String>> handleRiksbankApiException(RiksbankApiException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Riksbank API error: " + ex.getMessage());
    }

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
