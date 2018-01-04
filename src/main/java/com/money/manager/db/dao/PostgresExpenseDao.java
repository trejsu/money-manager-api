package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresExpenseDao implements ExpenseDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresExpenseDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Expense newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }
}
