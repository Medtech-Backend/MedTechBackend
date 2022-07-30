package com.project.medtech.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String message;
    public ResourceNotFoundException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
