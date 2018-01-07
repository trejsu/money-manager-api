package com.money.manager.db.dao;

import com.money.manager.db.Postgres;
import com.money.manager.model.Budget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PostgresBudgetDao implements BudgetDao {

    private final Postgres postgres;

    @Autowired
    public PostgresBudgetDao(Postgres postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Budget newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }
}
