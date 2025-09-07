package com.project.ExpenseTracker.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
