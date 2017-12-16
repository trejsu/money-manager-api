package com.money.manager.factory;

import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.HibernateWalletDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dao.WalletDao;
import com.money.manager.dto.NoExpensesWallet;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.LinkedList;

public class WalletFactory {

    private static final WalletDao walletDao = new HibernateWalletDao(new HibernateUserDao());

    public static NoExpensesWallet getSummaryWallet(User user) throws CustomException {
        BigDecimal amount = calculateAmount(user);
        return new NoExpensesWallet(
                0,
                amount,
                "wszystkie"
        );
    }

    private static BigDecimal calculateAmount(User user) throws CustomException {
        return walletDao.getSummaryAmountForUser(user);
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
