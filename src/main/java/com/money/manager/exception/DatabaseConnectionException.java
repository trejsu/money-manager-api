package com.money.manager.exception;

public class DatabaseConnectionException extends CustomException {
    public DatabaseConnectionException(String errorMessage) {
        super(errorMessage);
    }
}
