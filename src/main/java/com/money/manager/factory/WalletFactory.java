package com.money.manager.factory;

import com.money.manager.db.dao.WalletDao;
import com.money.manager.dto.WalletDto;
import com.money.manager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletFactory {

    private final WalletDao walletDao;

    @Autowired
    public WalletFactory(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    // todo: move to service
    public WalletDto getSummaryWallet(User user) {
        BigDecimal amount = calculateAmount(user);
        return new WalletDto(
                0,
                amount,
                "wszystkie"
        );
    }

    private BigDecimal calculateAmount(User user) {
        return walletDao.getSummaryAmountForUser(user);
    }
}
