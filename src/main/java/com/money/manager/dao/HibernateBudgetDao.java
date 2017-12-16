package com.money.manager.dao;

import com.money.manager.entity.Budget;
import com.money.manager.exception.CustomException;

import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;

public class HibernateBudgetDao implements BudgetDao {

    @Override
    public Integer add(Budget newInstance) throws CustomException {
        return executeQuery(session -> (Integer) session.save(newInstance));
    }

    @Override
    public Optional<Budget> get(Integer id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(Budget.class, id)));
    }

    @Override
    public void update(Budget transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public void delete(Budget persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        });
    }

    @Override
    public List<Budget> findAll() throws CustomException {
        return executeQuery(session ->
                        session
                            .createQuery("FROM Budget", Budget.class)
                            .list()
        );
    }
}
