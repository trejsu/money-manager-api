package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.entity.Category;
import com.money.manager.entity.Expense;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;

public interface ExpenseDao extends GenericDao<Expense, Integer> {
    BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) throws CustomException;
}
