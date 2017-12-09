package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.entity.Expense;
import com.money.manager.entity.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletDao extends GenericDao<Wallet, Integer> {
    void addExpense(String login, Integer id, Expense expense) throws CustomException;
    // todo: too many arguments
    List<Expense> getExpensesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod, Integer limit, String sort) throws CustomException;
    Expense getHighestExpenseByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException;
    Map<String, BigDecimal> getCountedCategoriesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException;
}
