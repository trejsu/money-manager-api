package com.money.manager.auth.authentication.login;

import com.money.manager.auth.authentication.Authenticator;
import com.money.manager.auth.authentication.GoogleAuthenticator;
import com.money.manager.auth.authentication.AuthenticationData;
import com.money.manager.db.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google_login")
public class GoogleLoginServlet extends LoginServlet {

    private final UserDao userDao;

    @Autowired
    public GoogleLoginServlet(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Authenticator getAuthenticator(AuthenticationData authenticationData) {
        return new GoogleAuthenticator(authenticationData.getToken(), userDao);
    }
}
