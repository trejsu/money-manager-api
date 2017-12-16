package com.money.manager.service.servlet;

import com.money.manager.dto.NoPasswordUser;
import com.money.manager.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.money.manager.factory.UserFactory.getNoPasswordUserFromUserEntity;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequestMapping("/logged_in")
public class LoggedInServlet {

    @GetMapping(value = "/status", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkLoggedIn(HttpServletRequest request) {
        return checkSession(
                request,
                httpSession -> ResponseEntity.ok().build(),
                () -> ResponseEntity.status(401).build()
        );
    }

    @GetMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getLoggedInUser(HttpServletRequest request) {
        return checkSession(
                request,
                httpSession -> {
                    User user = (User) httpSession.getAttribute("user");
                    NoPasswordUser noPasswordUser =
                            getNoPasswordUserFromUserEntity(user);
                    return ResponseEntity.ok().build();
                },
                () -> ResponseEntity.status(401).build()
        );
    }

    private ResponseEntity<Void> checkSession(
            HttpServletRequest request,
            Function<HttpSession, ResponseEntity<Void>> onSuccess,
            Supplier<ResponseEntity<Void>> onFailure
    ) {
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;
        if (loggedIn) {
            return onSuccess.apply(session);
        } else {
            return onFailure.get();
        }
    }
}
