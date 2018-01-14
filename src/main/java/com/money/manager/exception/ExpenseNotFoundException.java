package com.money.manager.exception;

public class ExpenseNotFoundException extends NotFoundException {
    public ExpenseNotFoundException(String message) {
        super(message);
    }
}