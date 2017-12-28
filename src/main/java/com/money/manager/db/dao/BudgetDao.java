package com.money.manager.db.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;
import com.money.manager.model.User;

import java.util.List;

public interface BudgetDao {
    void addToUser(Budget newInstance, User user);

    void update(Budget transientObject);

    List<Budget> getFromUserAndTimePeriod(User user, TimePeriod start, TimePeriod end);
}
