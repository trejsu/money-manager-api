package com.money.manager.services.servlet.register;

import com.money.manager.dto.RegistrantUser;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/google_register")
public class GoogleRegisterServlet extends RegisterServlet {
    @Override
    protected void setup(RegistrantUser registrantUser) {

    }
}
