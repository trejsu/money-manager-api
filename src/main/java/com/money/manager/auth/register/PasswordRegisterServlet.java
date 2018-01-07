package com.money.manager.auth.register;

import com.money.manager.auth.hash.Hasher;
import com.money.manager.auth.hash.PBKDF2Hasher;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password_register")
public class PasswordRegisterServlet extends RegisterServlet {

    private static Hasher hasher = new PBKDF2Hasher();

    @Override
    @SneakyThrows
    protected void setup(UserInputDto userInputDto) {
        userInputDto.setPassword(hasher.getSaltedHash(userInputDto.getPassword()));
    }
}
