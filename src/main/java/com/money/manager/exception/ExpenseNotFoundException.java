package com.money.manager.exception;

public class ExpenseNotFoundException extends CustomException {
    public ExpenseNotFoundException(String message) {
        super(message);
    }
}