package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao extends GenericCrudDao<User, String> {
    void addWallet(String login, Wallet wallet) throws CustomException;
    List<Budget> getBudgetsByLoginAndTimePeriod(String login, TimePeriod start, TimePeriod end) throws CustomException;
    List<Wallet> getWalletsByLogin(String login) throws CustomException;
    <T> void updateField(String login, String field, T value) throws CustomException;
    BigDecimal getSummaryAmount(String login) throws CustomException;
}
