package com.money.manager.factory;

import com.money.manager.dto.NoPasswordUser;
import com.money.manager.dto.RegistrantUser;
import com.money.manager.entity.User;

import java.util.LinkedList;

public class UserFactory {

    public static NoPasswordUser getNoPasswordUserFromUserEntity(User user) {
        return new NoPasswordUser(
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.isAdmin()
        );
    }

    public static User getUserEntityFromRegistrantUser(RegistrantUser registrantUser) {
        return new com.money.manager.entity.User(
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
