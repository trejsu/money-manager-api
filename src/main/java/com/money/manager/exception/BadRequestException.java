package com.money.manager.exception;

import org.springframework.http.ResponseEntity;

public abstract class BadRequestException extends RuntimeException {

    private Problem problem;

    BadRequestException(String problem, String solution) {
        super(problem);
        this.problem = new Problem(problem, solution);
    }

    public ResponseEntity<Problem> getResponseEntity() {
        return ResponseEntity.badRequest().body(problem);
    }
}
