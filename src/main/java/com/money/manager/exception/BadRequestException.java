package com.money.manager.exception;

public class BadRequestException extends CustomException {
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }
}
