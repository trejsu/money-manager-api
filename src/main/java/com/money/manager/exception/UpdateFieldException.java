package com.money.manager.exception;

public class UpdateFieldException extends BadRequestException {
    public UpdateFieldException(String problem, String solution) {
        super(problem, solution);
    }
}
