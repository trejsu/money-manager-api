package com.money.manager.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.UserDao;
import com.money.manager.entity.User;
import com.money.manager.exception.CustomException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

public class GoogleAuthenticator implements Authenticator {

    private static final String CLIENT_ID = "434476122553-nsqfrvsmc4g2ca8277epe8edult0omul.apps.googleusercontent.com";
    private static final JacksonFactory jacksonFactory = new JacksonFactory();
    private static final HttpTransport httpTransport = new NetHttpTransport();

    private static final UserDao userDao = new HibernateUserDao();

    private final String token;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthenticator(String token) {
        this.token = token;
        this.verifier = new GoogleIdTokenVerifier
                .Builder(httpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public Optional<User> verify() throws CustomException {
        GoogleIdToken verifiedToken = getVerifiedToken();
        if (verifiedToken == null) {
            return Optional.empty();
        }
        return userDao.get(getLogin(verifiedToken));
    }

    private String getLogin(GoogleIdToken verifiedToken) {
        Payload payload = verifiedToken.getPayload();
        String email = payload.getEmail();
        return email.split("@")[0];
    }

    private GoogleIdToken getVerifiedToken() {
        try {
            return verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }
}
