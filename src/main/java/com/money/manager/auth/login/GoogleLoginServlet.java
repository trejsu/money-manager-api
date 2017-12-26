package com.money.manager.auth.login;

import com.money.manager.auth.Authenticator;
import com.money.manager.auth.GoogleAuthenticator;
import com.money.manager.auth.AuthenticationData;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/google_login")
public class GoogleLoginServlet extends LoginServlet {

    @Override
    public Authenticator getAuthenticator(AuthenticationData authenticationData) {
        return new GoogleAuthenticator(authenticationData.getToken());
    }
}
