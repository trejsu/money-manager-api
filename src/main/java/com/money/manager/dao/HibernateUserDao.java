package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.entity.Budget;
import com.money.manager.entity.User;
import com.money.manager.entity.Wallet;
import com.money.manager.exception.BadRequestException;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.exception.UserNotFoundException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;
import static java.util.Comparator.comparingInt;

public class HibernateUserDao implements UserDao {

    private final static ExpenseDao expenseDao = new HibernateExpenseDao();

    @Override
    public String add(User newInstance) throws CustomException {
        checkLoginAvailability(newInstance);
        return executeQuery(session -> (String) session.save(newInstance),
                "Adding new user failed.");
    }

    private void checkLoginAvailability(User newInstance) throws CustomException {
        String login = newInstance.getLogin();
        if (get(login).isPresent()) {
            throw new LoginAlreadyTakenException(
                    "User with login " + login + " already exists!");
        }
    }

    @Override
    public Optional<User> get(String id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(User.class, id),
                "Retrieving user with id " + id + " failed."
        ));
    }

    @Override
    public void update(User transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        }, "Updating user failed.");
    }

    @Override
    public void delete(User persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        }, "Removing user failed.");
    }

    @Override
    public List<User> findAll() throws CustomException {
        return executeQuery(session -> session
                .createQuery("FROM User", User.class)
                .list(), "Retrieving users failed.");
    }

    @Override
    public void addWallet(String login, Wallet wallet) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        executeQuery(session -> {
            user.getWallets().add(wallet);
            update(user);
            return user;
        }, "Adding new wallet to " + login + " failed.");
    }

    @Override
    public List<Budget> getBudgetsByLoginAndTimePeriod(String login, TimePeriod start, TimePeriod end) throws CustomException {
        get(login).orElseThrow(() -> buildUserNotFoundException(login));
        return executeQuery(session -> {
            String query =
                    "SELECT b FROM User u " +
                    "JOIN u.budgets b " +
                    "WHERE u.login = :login " +
                    "AND b.start >= :start_min " +
                    "AND b.start <= :start_max " +
                    "AND b.end >= :end_min " +
                    "AND b.end <= :end_max " +
                    "ORDER BY b.id DESC";
            return session
                    .createQuery(query, Budget.class)
                    .setParameter("login", login)
                    .setParameter("start_min", start.getStart())
                    .setParameter("start_max" , start.getEnd())
                    .setParameter("end_min", end.getStart())
                    .setParameter("end_max" , end.getEnd())
                    .list();
        }, "Retrieving expenses failed");
    }

    @Override
    public List<Wallet> getWalletsByLogin(String login) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        return sortWalletsByID(user.getWallets());
    }

    private List<Wallet> sortWalletsByID(List<Wallet> wallets) {
        wallets.sort(comparingInt(Wallet::getId));
        return wallets;
    }

    @Override
    public <T> void updateField(String login, String field, T value) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        try {
            PropertyUtils.setProperty(user, field, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BadRequestException("Requested field cannot be updated. Validate entered data and try again.");
        }
        update(user);
    }

    @Override
    public BigDecimal getSummaryAmount(String login) throws CustomException {
        BigDecimal sum = executeQuery(session -> {
            String query =
                    "SELECT sum(w.amount) FROM User u " +
                            "JOIN u.wallets w " +
                            "WHERE u.login = :login";
            return session
                    .createQuery(query, BigDecimal.class)
                    .setParameter("login" , login)
                    .getSingleResult();
        }, "Retrieving wallets failed");
        if (sum == null) {
            return BigDecimal.valueOf(0);
        }
        return sum;
    }

    @Override
    public void addBudget(String login, Budget budget) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        budget.setCurrent(calculateCurrent(login, budget));
        executeQuery(session -> {
            user.getBudgets().add(budget);
            update(user);
            return user;
        }, "Adding new budget to " + login + " failed.");
    }

    private BigDecimal calculateCurrent(String login, Budget budget) throws CustomException {
        return expenseDao.getSummaryByUserTimePeriodAndCategory(
                login,
                new TimePeriod(
                        budget.getStart(),
                        budget.getEnd()
                ),
                budget.getCategory()
        );
    }

    private UserNotFoundException buildUserNotFoundException(String login) {
        return new UserNotFoundException("User with login " + login + " does not exists");
    }
}
