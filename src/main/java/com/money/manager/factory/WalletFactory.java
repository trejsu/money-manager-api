package com.money.manager.factory;

import com.money.manager.dao.HibernateUserDao;
import com.money.manager.dao.HibernateWalletDao;
import com.money.manager.dao.WalletDao;
import com.money.manager.dto.WalletDto;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;

import java.math.BigDecimal;
import java.util.LinkedList;

public class WalletFactory {

    private static final WalletDao walletDao = new HibernateWalletDao(new HibernateUserDao());

    // todo: move to service
    public static WalletDto getSummaryWallet(User user) throws CustomException {
        BigDecimal amount = calculateAmount(user);
        return new WalletDto(
                0,
                amount,
                "wszystkie"
        );
    }

    private static BigDecimal calculateAmount(User user) throws CustomException {
        return walletDao.getSummaryAmountForUser(user);
    }
}
