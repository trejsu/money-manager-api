package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostgresCategoryDao implements CategoryDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresCategoryDao(PostgresUtil postgres) {
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
