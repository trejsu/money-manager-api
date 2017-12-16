package com.money.manager.dao;

import com.money.manager.model.Category;
import com.money.manager.exception.CustomException;

import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;

public class HibernateCategoryDao implements CategoryDao {

    @Override
    public List<Category> findAll() throws CustomException {
        return executeQuery(session ->
            session
                .createQuery("FROM Category", Category.class)
                .list()
        );
    }
}
