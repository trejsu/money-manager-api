package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;

public interface ExpenseDao extends GenericCrudDao<Expense, Integer> {
    BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) throws CustomException;
}
