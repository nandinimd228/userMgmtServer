package com.example.server.exception;

public class UserAccountNotFoundException extends RuntimeException {
    public UserAccountNotFoundException() {
        super("User account not found");
    }
}