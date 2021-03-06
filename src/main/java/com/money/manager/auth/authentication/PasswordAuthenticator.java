package com.money.manager.auth.authentication;

import com.money.manager.auth.authentication.hash.Hasher;
import com.money.manager.auth.authentication.hash.PBKDF2Hasher;
import com.money.manager.db.dao.UserDao;
import com.money.manager.model.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;



public class PasswordAuthenticator implements Authenticator {

    private final UserDao userDao;
    private final String login;
    private final char[] password;

    private Hasher hasher;

    public PasswordAuthenticator(UserDao userDao, String login, char[] password) {
        this.userDao = userDao;
        this.login = login;
        this.password = password;
        this.hasher = new PBKDF2Hasher();
    }

    public void setHasher(Hasher hasher) {
        this.hasher = hasher;
    }

    @Override
    public Optional<User> verify() throws InvalidKeySpecException, NoSuchAlgorithmException {
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
