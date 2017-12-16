package com.money.manager.service.servlet.login;

import com.money.manager.auth.Authenticator;
import com.money.manager.dto.AuthenticationData;
import com.money.manager.entity.User;
import com.money.manager.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public abstract class LoginServlet {

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(AuthenticationData authenticationData, HttpServletRequest request) throws InvalidKeySpecException, NoSuchAlgorithmException, CustomException {
        Authenticator authenticator = getAuthenticator(authenticationData);
        Optional<User> userOptional = authenticator.verify();
        if (userOptional.isPresent()) {
            request.getSession().setAttribute("user", userOptional.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }

    public abstract Authenticator getAuthenticator(AuthenticationData authenticationData);
}
