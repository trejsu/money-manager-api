package com.money.manager.dao;

import com.money.manager.model.User;
import com.money.manager.exception.CustomException;

import java.io.Serializable;

// todo: rename
public interface GenericUserRelatedDao<T> {
    void addToUser(T newInstance, User user) throws CustomException;
}
