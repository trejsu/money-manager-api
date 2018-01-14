package com.money.manager.auth.register;

import com.money.manager.db.dao.UserDao;
import com.money.manager.exception.BadRequestException;
import com.money.manager.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public abstract class RegisterServlet {

    @Autowired
    private UserDao userDao;

    @SneakyThrows
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody @Valid UserInputDto userInputDto) {
        try {
            userDao.add(initialize(userInputDto));
            return ResponseEntity.created(new URI("/uri_to_change")).build();
        } catch (BadRequestException e) {
            return e.getResponseEntity();
        }
    }

    private User initialize(UserInputDto userInputDto) {
        setup(userInputDto);
        return userInputDto.toUser();
    }

    protected abstract void setup(UserInputDto registrantUser);

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserInputDto {

        @NotNull
        private String login;
        private String firstName;
        private String lastName;
        private char[] password;

        public User toUser() {
            return User.builder()
                    .login(login)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .admin(false)
                    .build();
        }
    }
}
