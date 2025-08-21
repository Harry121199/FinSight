package com.project.ExpenseTracker.exception;

public class BudgetNotFound extends RuntimeException {
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public BudgetNotFound(String message) {
        super(message);
    }
}
