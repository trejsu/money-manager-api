package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.exception.CustomException;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;

import java.math.BigDecimal;

public interface ExpenseDao {
    BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) throws CustomException;

    void addToUserAndWallet(User user, Wallet wallet, Expense expense) throws CustomException;
}
