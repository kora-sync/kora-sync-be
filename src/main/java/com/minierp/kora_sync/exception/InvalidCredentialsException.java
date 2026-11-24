package com.minierp.kora_sync.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid e-mail or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}