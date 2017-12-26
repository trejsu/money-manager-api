package com.money.manager.auth.register;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google_register")
public class GoogleRegisterServlet extends RegisterServlet {
    @Override
    protected void setup(UserInputDto userInputDto) {

    }
}
