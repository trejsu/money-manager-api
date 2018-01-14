package com.money.manager.auth.authentication.login;

import com.money.manager.auth.authentication.Authenticator;
import com.money.manager.auth.authentication.AuthenticationData;
import com.money.manager.exception.EmptyPasswordException;
import com.money.manager.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public abstract class LoginServlet {

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationData authenticationData, HttpServletRequest request) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Authenticator authenticator = getAuthenticator(authenticationData);
        Optional<User> userOptional;
        try {
            userOptional = authenticator.verify();
        } catch (EmptyPasswordException e) {
            return e.getResponseEntity();
        }
        if (userOptional.isPresent()) {
            request.getSession().setAttribute("user", userOptional.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    public abstract Authenticator getAuthenticator(AuthenticationData authenticationData);
}
