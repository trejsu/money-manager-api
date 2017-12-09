package com.money.manager.dao;

import com.money.manager.entity.Budget;
import com.money.manager.exception.CustomException;

import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;

public class HibernateBudgetDao implements BudgetDao {

    @Override
    public Integer add(Budget newInstance) throws CustomException {
        return executeQuery(session -> (Integer) session.save(newInstance),
                "Adding budget failed.");
    }

    @Override
    public Optional<Budget> get(Integer id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(Budget.class, id),
                "Retrieving budget with id " + id + " failed."
        ));
    }

    @Override
    public void update(Budget transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        }, "Updating budget failed.");
    }

    @Override
    public void delete(Budget persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        }, "Removing budget failed.");
    }

    @Override
    public List<Budget> findAll() throws CustomException {
        return executeQuery(session ->
                        session
                            .createQuery("FROM Budget", Budget.class)
                            .list()
                , "Retrieving budgets failed.");
    }
}
