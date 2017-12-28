package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.model.User;
import com.money.manager.exception.BadRequestException;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.exception.UserNotFoundException;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;


@Service
public class PostgresUserDao implements UserDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresUserDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public String add(User newInstance) {
        checkLoginAvailability(newInstance);
        return postgres.executeQuery(session -> (String) session.save(newInstance));
    }

    private void checkLoginAvailability(User newInstance) {
        String login = newInstance.getLogin();
        if (get(login).isPresent()) {
            throw new LoginAlreadyTakenException(
                    "User with login " + login + " already exists!");
        }
    }

    @Override
    public Optional<User> get(String id) {
        return Optional.ofNullable(postgres.executeQuery(
                session -> session.get(User.class, id)
        ));
    }

    @Override
    public void update(User transientObject) {
        postgres.executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public List<User> findAll() {
        return postgres.executeQuery(session -> session
                .createQuery("FROM User", User.class)
                .list());
    }

    @Override
    public <T> void updateField(String login, String field, T value) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        try {
            PropertyUtils.setProperty(user, field, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BadRequestException("Requested field cannot be updated. Validate entered data and try again.");
        }
        update(user);
    }

    private UserNotFoundException buildUserNotFoundException(String login) {
        return new UserNotFoundException("User with login " + login + " does not exists");
    }
}
