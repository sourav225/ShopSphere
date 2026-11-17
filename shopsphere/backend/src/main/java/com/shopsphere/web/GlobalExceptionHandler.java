package com.shopsphere.web;

import com.shopsphere.service.ApiException;
import java.util.Map;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, String>> api(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage()).findFirst().orElse("Invalid request");
        return ResponseEntity.badRequest().body(Map.of("message", msg));
    }

    /** Serializable transactions can abort under contention; tell the client to retry. */
    @ExceptionHandler(ConcurrencyFailureException.class)
    ResponseEntity<Map<String, String>> conflict(ConcurrencyFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Concurrent update, please retry"));
    }
}
