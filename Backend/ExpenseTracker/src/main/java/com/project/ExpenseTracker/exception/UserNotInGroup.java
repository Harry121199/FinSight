package com.project.ExpenseTracker.exception;

public class UserNotInGroup extends RuntimeException {
    public UserNotInGroup(String message) {
        super(message);
    }
}
