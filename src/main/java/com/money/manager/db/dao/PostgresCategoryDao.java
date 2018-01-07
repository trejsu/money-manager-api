package com.money.manager.db.dao;

import com.money.manager.db.Postgres;
import com.money.manager.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostgresCategoryDao implements CategoryDao {

    private final Postgres postgres;

    @Autowired
    public PostgresCategoryDao(Postgres postgres) {
        this.postgres = postgres;
    }

    @Override
    public List<Category> findAll() {
        return postgres.executeQuery(session ->
            session
                .createQuery("FROM Category", Category.class)
                .list()
        );
    }
}
