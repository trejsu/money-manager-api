package com.money.manager.exception;

public class EmptyPasswordException extends BadRequestException {
    public EmptyPasswordException(String problem, String solution) {
        super(problem, solution);
    }
}
