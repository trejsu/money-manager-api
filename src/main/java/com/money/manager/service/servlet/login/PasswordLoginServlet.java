package com.money.manager.service.servlet.login;

import com.money.manager.auth.Authenticator;
import com.money.manager.auth.PasswordAuthenticator;
import com.money.manager.dto.AuthenticationData;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/password_login")
public class PasswordLoginServlet extends LoginServlet {

    @Override
    public Authenticator getAuthenticator(AuthenticationData authenticationData) {
        return new PasswordAuthenticator(
                authenticationData.getLogin(),
                authenticationData.getPassword()
        );
    }
}
