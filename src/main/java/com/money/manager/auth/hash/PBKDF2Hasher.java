package com.money.manager.auth.hash;

import com.google.api.client.util.Base64;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PBKDF2Hasher implements Hasher {

    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    @Override
    @SneakyThrows
    public char[] getSaltedHash(char[] password) {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return (Base64.encodeBase64String(salt) + "$" + hash(password, salt)).toCharArray();
    }

    @Override
    public boolean check(char[] password, char[] stored) {
        String[] saltAndPass = String.copyValueOf(stored).split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    @SneakyThrows
    private static String hash(char[] password, byte[] salt) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Empty passwords are not supported.");
        }
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(
                password,
                salt,
                iterations,
                desiredKeyLen
        ));
        return Base64.encodeBase64String(secretKey.getEncoded());
    }
}
