package com.money.manager.auth.login;

import com.money.manager.dto.UserDto;
import com.money.manager.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
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
    public ResponseEntity<UserDto> getLoggedInUser(HttpServletRequest request) {
        return checkSession(
                request,
                httpSession -> {
                    User user = (User) httpSession.getAttribute("user");
                    UserDto userDto = UserDto.fromUser(user);
                    return new ResponseEntity<>(userDto, HttpStatus.OK);
                },
                () -> ResponseEntity.status(401).build()
        );
    }

    private <T> ResponseEntity<T> checkSession(
            HttpServletRequest request,
            Function<HttpSession, ResponseEntity<T>> onSuccess,
            Supplier<ResponseEntity<T>> onFailure
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