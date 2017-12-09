package com.money.manager.auth;

import com.money.manager.auth.hash.Hasher;
import com.money.manager.auth.hash.PBKDF2Hasher;
import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.UserDao;
import com.money.manager.entity.User;
import com.money.manager.exception.CustomException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

public class PasswordAuthenticator implements Authenticator {

    private final static UserDao userDao = new HibernateUserDao();

    private final String login;
    private final char[] password;

    private Hasher hasher;

    public PasswordAuthenticator(String login, char[] password) {
        this.login = login;
        this.password = password;
        this.hasher = new PBKDF2Hasher();
    }

    public void setHasher(Hasher hasher) {
        this.hasher = hasher;
    }

    @Override
    public Optional<User> verify() throws CustomException, InvalidKeySpecException, NoSuchAlgorithmException {
        Optional<User> userOptional = userDao.get(login);
        if (userOptional.isPresent() && passwordPresent(userOptional.get())) {
            if (hasher.check(password, userOptional.get().getPassword())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }

    private boolean passwordPresent(User user) {
        return user.getPassword() != null;
    }
}
