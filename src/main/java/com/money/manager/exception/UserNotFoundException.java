package com.money.manager.exception;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String login) {
        super("User with login " + login + " was not found.", "Check if provided login is correct and try again.");
    }
}
