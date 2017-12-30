package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.util.Optional.ofNullable;

@Service
public class PostgresExpenseDao implements ExpenseDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresExpenseDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) {
        BigDecimal sum = postgres.executeQuery(session -> {
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
        });
        return ofNullable(sum).orElse(ZERO);
    }

    @Override
    public Integer add(Expense newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }
}
