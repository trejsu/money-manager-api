package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletDao {
    // todo: too many arguments
    List<Expense> getExpensesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod, Integer limit, String sort) throws CustomException;
    Expense getHighestExpenseByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException;
    Map<String, BigDecimal> getCountedCategoriesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException;

    void addToUser(Wallet newInstance, User user) throws CustomException;

    List<Wallet> getAllFromUser(User user) throws CustomException;

    BigDecimal getSummaryAmountForUser(User user) throws CustomException;
}
