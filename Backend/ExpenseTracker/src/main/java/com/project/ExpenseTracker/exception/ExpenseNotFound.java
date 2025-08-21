package com.project.ExpenseTracker.exception;

public class ExpenseNotFound extends RuntimeException {
    public ExpenseNotFound(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
