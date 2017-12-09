package com.money.manager.auth.hash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface Hasher {
    char[] getSaltedHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException;
    boolean check(char[] password, char[] stored) throws InvalidKeySpecException, NoSuchAlgorithmException;
}
