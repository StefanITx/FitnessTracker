package com.fitnesstracker.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyTaken(EmailAlreadyTakenException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,String>> handleInvalidCredentialsException(InvalidCredentialsException ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


}