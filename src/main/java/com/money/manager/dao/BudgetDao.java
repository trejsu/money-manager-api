package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.exception.CustomException;
import com.money.manager.model.Budget;
import com.money.manager.model.User;

import java.util.List;

public interface BudgetDao {
    void addToUser(Budget newInstance, User user) throws CustomException;

    void update(Budget transientObject) throws CustomException;

    List<Budget> getFromUserAndTimePeriod(User user, TimePeriod start, TimePeriod end) throws CustomException;
}
