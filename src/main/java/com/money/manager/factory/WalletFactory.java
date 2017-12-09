package com.money.manager.factory;

import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dto.NoExpensesWallet;
import com.money.manager.entity.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.LinkedList;

public class WalletFactory {

    private static final UserDao userDao = new HibernateUserDao();

    public static NoExpensesWallet getSummaryWallet(String login) throws CustomException {
        BigDecimal amount = calculateAmount(login);
        return new NoExpensesWallet(
                0,
                amount,
                "wszystkie"
        );
    }

    private static BigDecimal calculateAmount(String login) throws CustomException {
        return userDao.getSummaryAmount(login);
    }

    public static NoExpensesWallet getNoExpensesWalletFromWalletEntity(Wallet wallet) {
        return new NoExpensesWallet(
                wallet.getId(),
                wallet.getAmount(),
                wallet.getName()
        );
    }

    public static Wallet getWalletEntityFromNoExpensesWallet(NoExpensesWallet noExpensesWallet) {
        return new Wallet(
                noExpensesWallet.getId(),
                noExpensesWallet.getAmount(),
                noExpensesWallet.getName(),
                new LinkedList<>()
        );
    }
}
