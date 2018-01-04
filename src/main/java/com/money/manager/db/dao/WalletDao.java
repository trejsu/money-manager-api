package com.money.manager.db.dao;

import com.money.manager.model.User;
import com.money.manager.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletDao {

    List<Wallet> getAllFromUser(User user);

    BigDecimal getSummaryAmountForUser(User user);

    Integer add(Wallet newInstance);

    void update(Wallet transientObject);

}
