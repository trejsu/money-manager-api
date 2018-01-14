package com.money.manager.exception;

public class ExpenseNotFoundException extends NotFoundException {

    public ExpenseNotFoundException(Integer id) {
        super("Expense with id " + id + " was not found.", "Check if provided id is correct and try again.");
    }
}