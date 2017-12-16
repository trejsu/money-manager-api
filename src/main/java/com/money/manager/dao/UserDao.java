package com.money.manager.dao;

import com.money.manager.model.User;
import com.money.manager.exception.CustomException;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    String add(User newInstance) throws CustomException;

    Optional<User> get(String id) throws CustomException;

    void update(User transientObject) throws CustomException;

    List<User> findAll() throws CustomException;

    <T> void updateField(String login, String field, T value) throws CustomException;
}
