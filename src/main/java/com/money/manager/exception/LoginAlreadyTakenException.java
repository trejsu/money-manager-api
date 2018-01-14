package com.money.manager.exception;

public class LoginAlreadyTakenException extends BadRequestException {
    public LoginAlreadyTakenException(String problem, String solution) {
        super(problem, solution);
    }
}
