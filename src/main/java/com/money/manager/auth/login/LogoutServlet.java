package com.money.manager.auth.login;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/logout")
public class LogoutServlet {
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
