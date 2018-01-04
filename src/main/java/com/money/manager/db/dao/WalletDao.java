package com.money.manager.db.dao;

import com.money.manager.model.User;
import com.money.manager.model.Wallet;

import java.util.List;

public interface WalletDao {

    List<Wallet> getAllFromUser(User user);

    Integer add(Wallet newInstance);

    void update(Wallet transientObject);

}
