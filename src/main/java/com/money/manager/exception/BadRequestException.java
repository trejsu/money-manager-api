package com.money.manager.exception;

import org.springframework.http.ResponseEntity;

public abstract class BadRequestException extends ErrorResponseException {

    BadRequestException(String problem, String solution) {
        super(problem, solution);
    }

    public ResponseEntity<Problem> getResponseEntity() {
        return ResponseEntity.badRequest().body(problem);
    }
}
