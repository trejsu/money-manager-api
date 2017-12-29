package com.money.manager.db.dao;

import com.money.manager.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    String add(User newInstance);

    Optional<User> get(String id);

    void update(User transientObject);

    List<User> findAll();
}
