package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.exception.CustomException;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.money.manager.util.HibernateUtil.executeQuery;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
public class HibernateExpenseDao implements ExpenseDao {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;

    private final BudgetDao budgetDao;

    public HibernateExpenseDao(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
    }

    @Override
    public BigDecimal getSummaryByUserTimePeriodAndCategory(String login, TimePeriod timePeriod, Category category) throws CustomException {
        BigDecimal sum = executeQuery(session -> {
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
        if (sum == null) {
            return BigDecimal.valueOf(0);
        }
        return sum;
    }

    @Override
    public void addToUserAndWallet(User user, Wallet wallet, Expense expense) throws CustomException {
        executeQuery(session -> {
            expense.setDate(getToday());
            // todo: fix atomicity
//            expenseDao.add(expense);
            wallet.getExpenses().add(expense);
            updateAmount(wallet, expense);
            return session.save(wallet);
        });
        updateBudgets(user, expense);
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

    private void updateBudgets(User user, Expense expense) throws CustomException {
        List<Budget> budgets = budgetDao.getFromUserAndTimePeriod(user, new TimePeriod(null, getToday()), new TimePeriod(getToday(), null));
        for (Budget budget : budgets) {
            if (budget.getCategory().getName().equals(expense.getCategory().getName())) {
                budget.setCurrent(budget.getCurrent().add(expense.getAmount()));
                budgetDao.update(budget);
            }
        }
    }


}
