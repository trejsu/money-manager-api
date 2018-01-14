package com.money.manager.exception;

import org.springframework.http.ResponseEntity;

public abstract class NotFoundException extends ErrorResponseException {

    NotFoundException(String problem, String solution) {
        super(problem, solution);
    }

    public ResponseEntity<Problem> getResponseEntity() {
        return ResponseEntity.status(404).body(problem);
    }
}
