package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.model.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PostgresBudgetDao implements BudgetDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresBudgetDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Budget newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }
}
