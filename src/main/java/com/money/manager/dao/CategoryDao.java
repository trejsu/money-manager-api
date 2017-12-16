package com.money.manager.dao;

import com.money.manager.exception.CustomException;
import com.money.manager.model.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> findAll() throws CustomException;
}
