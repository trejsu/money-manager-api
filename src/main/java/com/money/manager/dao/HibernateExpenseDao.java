package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.entity.Category;
import com.money.manager.entity.Expense;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;

public class HibernateExpenseDao implements ExpenseDao {

    @Override
    public Integer add(Expense newInstance) throws CustomException {
        return executeQuery(session -> (Integer) session.save(newInstance),
                "Adding expense failed.");
    }

    @Override
    public Optional<Expense> get(Integer id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(Expense.class, id),
                "Retrieving expense with id " + id + " failed."
        ));
    }


    @Override
    public void update(Expense transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        }, "Updating expense failed.");
    }

    @Override
    public void delete(Expense persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        }, "Removing expense failed.");
    }

    @Override
    public List<Expense> findAll() throws CustomException {
        return executeQuery(session ->
                        session
                                .createQuery("FROM Expense", Expense.class)
                                .list()
                , "Retrieving categories failed.");
    }

    @Override
    public BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) throws CustomException {
        BigDecimal sum = executeQuery(session -> {
            String query =
                    "SELECT sum(e.amount) FROM User u " +
                    "JOIN u.wallets w " +
                    "JOIN w.expenses e " +
                    "JOIN e.category c " +
                    "WHERE u.login = :login " +
                    "AND c.name = :categoryName " +
                    "AND c.profit = :categoryProfit " +
                    "AND e.date >= :start " +
                    "AND e.date <= :end";
            return session
                    .createQuery(query, BigDecimal.class)
                    .setParameter("login", login)
                    .setParameter("categoryName", category.getName())
                    .setParameter("categoryProfit", category.isProfit())
                    .setParameter("start", timePeriod.getStart())
                    .setParameter("end" , timePeriod.getEnd())
                    .getSingleResult();
        }, "Retrieving expenses failed");
        if (sum == null) {
            return BigDecimal.valueOf(0);
        }
        return sum;
    }
}
