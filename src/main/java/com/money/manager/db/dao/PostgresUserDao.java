package com.money.manager.db.dao;

import com.money.manager.db.Postgres;
import com.money.manager.model.User;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PostgresUserDao implements UserDao {

    private final Postgres postgres;

    @Autowired
    public PostgresUserDao(Postgres postgres) {
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
                    "User with login " + login + " already exists!",
                    "Pick another login and try again"
            );
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
}
