package com.money.manager.exception;


public abstract class NotFoundException extends ErrorResponseException {
    NotFoundException(String problem, String solution) {
        super(problem, solution);
    }
}
