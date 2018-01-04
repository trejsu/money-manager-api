package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.dto.TimePeriod;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparingInt;

@Service
public class PostgresWalletDao implements WalletDao {

    private final PostgresUtil postgres;

    @Autowired
    public PostgresWalletDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Wallet newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }

    @Override
    public void update(Wallet transientObject) {
        postgres.executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public List<Wallet> getAllFromUser(User user) throws CustomException {
        final List<Wallet> wallets = user.getWallets();
        wallets.sort(comparingInt(Wallet::getId));
        return wallets;
    }

    @Override
    public BigDecimal getSummaryAmountForUser(User user) {
        BigDecimal sum = postgres.executeQuery(session -> {
            String query =
                    "SELECT sum(w.amount) FROM User u " +
                            "JOIN u.wallets w " +
                            "WHERE u.login = :login";
            return session
                    .createQuery(query, BigDecimal.class)
                    .setParameter("login" , user.getLogin())
                    .getSingleResult();
        });
        return Optional.ofNullable(sum).orElse(BigDecimal.ZERO);
    }
}
