package com.money.manager.auth.authentication.login;

import com.money.manager.auth.authentication.Authenticator;
import com.money.manager.auth.authentication.PasswordAuthenticator;
import com.money.manager.auth.authentication.AuthenticationData;
import com.money.manager.db.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password_login")
public class PasswordLoginServlet extends LoginServlet {

    private final UserDao userDao;

    @Autowired
    public PasswordLoginServlet(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Authenticator getAuthenticator(AuthenticationData authenticationData) {
        return new PasswordAuthenticator(
                userDao,
                authenticationData.getLogin(),
                authenticationData.getPassword()
        );
    }
}
