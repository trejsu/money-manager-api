package com.money.manager.db.dao;

import com.money.manager.model.Expense;

public interface ExpenseDao {
    Integer add(Expense newInstance);
    void remove(Integer id);
}
