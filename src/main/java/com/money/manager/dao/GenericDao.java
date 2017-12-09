package com.money.manager.dao;

import com.money.manager.exception.CustomException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericDao <T, PK extends Serializable> {
    PK add(T newInstance) throws CustomException;
    Optional<T> get(PK id) throws CustomException;
    void update(T transientObject) throws CustomException;
    void delete(T persistentObject) throws CustomException;
    List<T> findAll() throws CustomException;
}
