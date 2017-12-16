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

    @Override
    public Category.CategoryPK add(Category newInstance) throws CustomException {
        return executeQuery(session -> (Category.CategoryPK) session.save(newInstance));
    }

    @Override
    public Optional<Category> get(Category.CategoryPK id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(Category.class, id)));
    }

    @Override
    public void update(Category transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public void delete(Category persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        });
    }
}
