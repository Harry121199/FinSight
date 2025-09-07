package com.project.ExpenseTracker.exception;

public class SecurityException extends RuntimeException {
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public SecurityException(String message) {
        super(message);
    }
}
