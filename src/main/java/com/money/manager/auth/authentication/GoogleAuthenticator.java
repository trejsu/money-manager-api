package com.money.manager.auth.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.money.manager.db.dao.UserDao;
import com.money.manager.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class GoogleAuthenticator implements Authenticator {

    private static final String CLIENT_ID = "434476122553-nsqfrvsmc4g2ca8277epe8edult0omul.apps.googleusercontent.com";
    private static final JacksonFactory jacksonFactory = new JacksonFactory();
    private static final HttpTransport httpTransport = new NetHttpTransport();

    private final UserDao userDao;

    private final String token;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthenticator(String token, UserDao userDao) {
        this.token = token;
        this.userDao = userDao;
        this.verifier = new GoogleIdTokenVerifier
                .Builder(httpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public Optional<User> verify() {
        Optional<GoogleIdToken> verifiedToken = getVerifiedToken();
        return verifiedToken
                .map(token -> userDao.get(getLogin(token)))
                .orElse(empty());
    }

    private String getLogin(GoogleIdToken verifiedToken) {
        Payload payload = verifiedToken.getPayload();
        String email = payload.getEmail();
        return email.split("@")[0];
    }

    private Optional<GoogleIdToken> getVerifiedToken() {
        try {
            return of(verifier.verify(token));
        } catch (GeneralSecurityException | IOException e) {
            return empty();
        }
    }
}
