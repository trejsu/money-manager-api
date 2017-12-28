package com.money.manager.db.dao;

import com.money.manager.model.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> findAll();
}
