package com.money.manager.api;

import com.money.manager.exception.BadRequestException;
import com.money.manager.exception.NotFoundException;
import com.money.manager.exception.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    Problem handleException(BadRequestException ex) {
        return ex.getProblem();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    Problem handleException(NotFoundException ex) {
        return ex.getProblem();
    }
}