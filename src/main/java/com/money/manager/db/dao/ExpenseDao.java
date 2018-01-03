package com.money.manager.db.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;

import java.math.BigDecimal;

public interface ExpenseDao {

    BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category);
    Integer add(Expense newInstance);

}
