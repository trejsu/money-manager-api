package com.money.manager.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String errorMessage) {
        super(errorMessage);
    }
}
