package com.money.manager.auth;

import com.money.manager.model.User;
import com.money.manager.exception.CustomException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public interface Authenticator {
    Optional<User> verify() throws CustomException, InvalidKeySpecException, NoSuchAlgorithmException;
}
