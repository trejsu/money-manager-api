package com.money.manager.factory;

import com.money.manager.dto.RegistrantUser;
import com.money.manager.model.User;

import java.util.LinkedList;

public class UserFactory {

    public static User getUserEntityFromRegistrantUser(RegistrantUser registrantUser) {
        return new com.money.manager.model.User(
                registrantUser.getLogin(),
                registrantUser.getFirstName(),
                registrantUser.getLastName(),
                registrantUser.getPassword(),
                false,
                new LinkedList<>(),
                new LinkedList<>(),
                new LinkedList<>()
        );
    }
}
