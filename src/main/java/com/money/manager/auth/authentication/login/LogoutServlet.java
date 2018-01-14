package com.money.manager.auth.authentication.login;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/logout")
public class LogoutServlet {

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ofNullable(session).ifPresent(HttpSession::invalidate);
        return ResponseEntity.ok().build();
    }
}
