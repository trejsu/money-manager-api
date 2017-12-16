package com.money.manager.dao;

import com.money.manager.model.Budget;
import com.money.manager.model.User;
import com.money.manager.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;

@Service
public class HibernateBudgetDao implements BudgetDao {

    private final UserDao userDao;

    @Autowired
    public HibernateBudgetDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addToUser(Budget newInstance, User user) throws CustomException {
        executeQuery(session -> {
            user.getBudgets().add(newInstance);
            userDao.update(user);
            return user;
        });
    }

    @Override
    public Integer add(Budget newInstance) throws CustomException {
        return null;
    }

    @Override
    public Optional<Budget> get(Integer id) throws CustomException {
        return null;
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

    }

    @Override
    public List<Budget> findAll() throws CustomException {
        return null;
    }
}
