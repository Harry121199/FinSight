package com.project.ExpenseTracker.exception;

public class BudgetAlreadyExists extends RuntimeException {
    public BudgetAlreadyExists(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
