package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.math.BigDecimal.ZERO;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Optional.ofNullable;

@Service
public class PostgresExpenseDao implements ExpenseDao {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;

    private final PostgresUtil postgres;

    @Autowired
    public PostgresExpenseDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) {
        BigDecimal sum = postgres.executeQuery(session -> {
            String query =
                    "SELECT sum(e.amount) FROM User u " +
                    "JOIN u.wallets w " +
                    "JOIN w.expenses e " +
                    "JOIN e.category c " +
                    "WHERE u.login = :login " +
                    "AND c.name = :categoryName " +
                    "AND c.profit = :categoryProfit " +
                    "AND e.date >= :start " +
                    "AND e.date <= :end";
            return session
                    .createQuery(query, BigDecimal.class)
                    .setParameter("login", login)
                    .setParameter("categoryName", category.getName())
                    .setParameter("categoryProfit", category.isProfit())
                    .setParameter("start", timePeriod.getStart())
                    .setParameter("end" , timePeriod.getEnd())
                    .getSingleResult();
        });
        return ofNullable(sum).orElse(ZERO);
    }

    @Override
    public void addToUserAndWallet(User user, Wallet wallet, Expense expense) {
        postgres.executeQuery(session -> {
            expense.setDate(getToday());
            session.save(expense);
            wallet.getExpenses().add(expense);
            updateAmount(wallet, expense);
            session.update(wallet);
            return null;
        });
    }

    private String getToday() {
        return LocalDate.now().format(DATE_TIME_FORMATTER);
    }

    private void updateAmount(Wallet wallet, Expense expense) {
        BigDecimal amount = expense.getAmount();
        if (!expense.getCategory().isProfit()) {
            amount = amount.negate();
        }
        wallet.setAmount(wallet.getAmount().add(amount));
    }
}
