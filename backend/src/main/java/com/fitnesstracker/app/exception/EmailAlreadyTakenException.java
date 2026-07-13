package com.fitnesstracker.app.exception;

public class EmailAlreadyTakenException extends RuntimeException{
    public EmailAlreadyTakenException(String email){
        super("Email already in use: " + email);
    }
}
