package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
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
    public void update(Budget transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public List<Budget> getFromUserAndTimePeriod(User user, TimePeriod start, TimePeriod end) throws CustomException {
        return executeQuery(session -> {
            String query =
                    "SELECT b FROM User u " +
                            "JOIN u.budgets b " +
                            "WHERE u.login = :login " +
                            "AND b.start >= :start_min " +
                            "AND b.start <= :start_max " +
                            "AND b.end >= :end_min " +
                            "AND b.end <= :end_max " +
                            "ORDER BY b.id DESC";
            return session
                    .createQuery(query, Budget.class)
                    .setParameter("login", user.getLogin())
                    .setParameter("start_min", start.getStart())
                    .setParameter("start_max" , start.getEnd())
                    .setParameter("end_min", end.getStart())
                    .setParameter("end_max" , end.getEnd())
                    .list();
        });
    }
}
