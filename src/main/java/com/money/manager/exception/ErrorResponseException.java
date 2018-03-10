package com.money.manager.exception;


public abstract class ErrorResponseException extends RuntimeException {

    Problem problem;

    ErrorResponseException(String problem, String solution) {
        super(problem);
        this.problem = new Problem(problem, solution);
    }

    public Problem getProblem() {
        return problem;
    }
}
