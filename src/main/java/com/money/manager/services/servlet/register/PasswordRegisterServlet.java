package com.money.manager.services.servlet.register;

import com.money.manager.auth.hash.Hasher;
import com.money.manager.auth.hash.PBKDF2Hasher;
import com.money.manager.dto.RegistrantUser;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RequestMapping("/password_register")
public class PasswordRegisterServlet extends RegisterServlet {

    private static Hasher hasher = new PBKDF2Hasher();

    public void setHasher(Hasher newHasher) {
        hasher = newHasher;
    }

    @Override
    protected void setup(RegistrantUser registrantUser) throws InvalidKeySpecException, NoSuchAlgorithmException {
        registrantUser.setPassword(hasher.getSaltedHash(registrantUser.getPassword()));
    }
}
