package com.money.manager.auth.authentication;

import com.money.manager.model.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public interface Authenticator {
    Optional<User> verify() throws InvalidKeySpecException, NoSuchAlgorithmException;
}
