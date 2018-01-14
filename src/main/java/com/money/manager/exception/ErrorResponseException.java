package com.money.manager.exception;

import org.springframework.http.ResponseEntity;

public abstract class ErrorResponseException extends RuntimeException {

    Problem problem;

    ErrorResponseException(String problem, String solution) {
        super(problem);
        this.problem = new Problem(problem, solution);
    }

    public abstract ResponseEntity<Problem> getResponseEntity();
}
