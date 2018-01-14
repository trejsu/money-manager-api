package com.money.manager.db.dao;

import com.money.manager.db.Postgres;
import com.money.manager.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresExpenseDao implements ExpenseDao {

    private final Postgres postgres;

    @Autowired
    public PostgresExpenseDao(Postgres postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Expense newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }

    @Override
    public void remove(Expense toDelete) {
        postgres.executeQuery(session -> {
            session.remove(toDelete);
            return true;
        });
    }
}
