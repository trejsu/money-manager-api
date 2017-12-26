package com.money.manager.auth.register;

import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dto.RegistrantUser;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public abstract class RegisterServlet {

    private final static UserDao userDao = new HibernateUserDao();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody UserInputDto userInputDto) throws CustomException {
        try {
            userDao.add(initialize(userInputDto));
            return ResponseEntity.ok().build();
        } catch (LoginAlreadyTakenException e) {
            return ResponseEntity.ok().build();
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
