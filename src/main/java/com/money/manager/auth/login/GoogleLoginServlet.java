package com.money.manager.auth.login;

import com.money.manager.auth.Authenticator;
import com.money.manager.auth.GoogleAuthenticator;
import com.money.manager.auth.AuthenticationData;
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
