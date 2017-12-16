package com.money.manager.service.servlet.register;

import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dto.RegistrantUser;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.factory.UserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public abstract class RegisterServlet {

    private final static UserDao userDao = new HibernateUserDao();

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(RegistrantUser registrantUser) throws InvalidKeySpecException, NoSuchAlgorithmException, CustomException {
        try {
            userDao.add(initialize(registrantUser));
            return ResponseEntity.created(null).build();
        } catch (LoginAlreadyTakenException e) {
            return ResponseEntity.ok().build();
        }
    }

    private com.money.manager.entity.User initialize(RegistrantUser registrantUser) throws InvalidKeySpecException, NoSuchAlgorithmException {
        setup(registrantUser);
        return UserFactory.getUserEntityFromRegistrantUser(registrantUser);
    }

    protected abstract void setup(RegistrantUser registrantUser) throws InvalidKeySpecException, NoSuchAlgorithmException;
}
