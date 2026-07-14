package com.fitnesstracker.app.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials!");
    }
}
