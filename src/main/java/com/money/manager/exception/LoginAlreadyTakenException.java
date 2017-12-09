package com.money.manager.exception;

public class LoginAlreadyTakenException extends CustomException {
    public LoginAlreadyTakenException(String message) {
        super(message);
    }
}
