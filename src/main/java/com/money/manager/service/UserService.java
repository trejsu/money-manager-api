package com.money.manager.service;

import com.money.manager.db.dao.BudgetDao;
import com.money.manager.db.dao.ExpenseDao;
import com.money.manager.db.dao.UserDao;
import com.money.manager.db.dao.WalletDao;
import com.money.manager.dto.BudgetOutputDto;
import com.money.manager.dto.ExpenseDto;
import com.money.manager.dto.Summary;
import com.money.manager.dto.TimePeriod;
import com.money.manager.dto.UserDto;
import com.money.manager.dto.WalletDto;
import com.money.manager.exception.BadRequestException;
import com.money.manager.exception.UserNotFoundException;
import com.money.manager.exception.WalletNotFoundException;
import com.money.manager.factory.WalletFactory;
import com.money.manager.model.Budget;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;


@Service
public class UserService {

    private final UserDao userDao;
    private final WalletDao walletDao;
    private final BudgetDao budgetDao;
    private final ExpenseDao expenseDao;
    private final WalletFactory walletFactory;

    @Autowired
    public UserService(UserDao userDao, WalletDao walletDao, BudgetDao budgetDao, ExpenseDao expenseDao, WalletFactory walletFactory) {
        this.userDao = userDao;
        this.walletDao = walletDao;
        this.budgetDao = budgetDao;
        this.expenseDao = expenseDao;
        this.walletFactory = walletFactory;
    }

    public List<UserDto> getUsers() {
        return userDao
                .findAll()
                .stream()
                .map(UserDto::fromUser)
                .collect(toList());
    }

    public <T> void updateUser(String login, String field, LinkedHashMap<String, T> value) {
        User user = getUser(login);
        try {
            PropertyUtils.setProperty(user, field, value.get(field));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BadRequestException("Requested field cannot be updated. Validate entered data and try again.");
        }
        userDao.update(user);
    }

    public List<WalletDto> getWallets(String login) {
        User user = getUser(login);
        List<WalletDto> wallets = new LinkedList<>(singletonList(walletFactory.getSummaryWallet(user)));
        wallets.addAll(walletDao
                .getAllFromUser(user)
                .stream()
                .map(WalletDto::fromWallet)
                .collect(toList()));
        return wallets;
    }

    public Integer addWallet(String login, WalletDto walletDto) {
        User user = getUser(login);
        final Wallet wallet = walletDto.toWallet();
        Integer id = walletDao.add(wallet);
        user.getWallets().add(wallet);
        userDao.update(user);
        return id;
    }

    public Summary getSummary(String login, Integer id, TimePeriod timePeriod) {
        return calculateSummary(getExpenses(login, id, timePeriod));
    }

    public List<Expense> getExpenses(String login, Integer id, TimePeriod timePeriod) {
        User user = getUser(login);
        return id == 0 ? getAllExpenses(user, timePeriod) : getExpensesFromWallet(user, id, timePeriod);
    }

    public Expense getHighestExpense(String login, Integer id, TimePeriod timePeriod) {
        return walletDao.getHighestExpenseByWalletAndTimePeriod(login, id, timePeriod);
    }

    public void addExpense(String login, Integer id, ExpenseDto expense) {
        User user = getUser(login);
        Wallet wallet = getWallet(id, user);
        expenseDao.addToUserAndWallet(user, wallet, expense.toExpense());
    }

    public Map<String, BigDecimal> getCountedCategories(String login, Integer id, TimePeriod timePeriod) {
        return walletDao.getCountedCategoriesByWalletAndTimePeriod(login, id, timePeriod);
    }

    public List<BudgetOutputDto> getBudgets(String login, TimePeriod start, TimePeriod end) {
        User user = getUser(login);
        final List<Budget> budgets = budgetDao.getFromUserAndTimePeriod(user, start, end);
        return budgets
                .stream()
                .map(budget -> BudgetOutputDto.fromBudget(budget, calculateCurrent(login, budget)))
                .collect(toList());
    }

    public void addBudget(String login, Budget budget) {
        User user = getUser(login);
        budgetDao.addToUser(budget, user);
    }

    private Summary calculateSummary(List<Expense> expenses) {
        BigDecimal inflow = new BigDecimal(0);
        BigDecimal outflow = new BigDecimal(0);
        for (Expense expense : expenses) {
            BigDecimal amount = expense.getAmount();
            if (expense.getCategory().isProfit()) {
                inflow = inflow.add(amount);
            } else {
                outflow = outflow.add(amount);
            }
        }
        return new Summary(inflow, outflow);
    }

    private BigDecimal calculateCurrent(String login, Budget budget) {
        return expenseDao.getSummaryByUserTimePeriodAndCategory(
                login,
                new TimePeriod(budget.getStart(), budget.getEnd()),
                budget.getCategory()
        );
    }

    private User getUser(String login) {
        return userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
    }

    private Wallet getWallet(Integer id, User user) {
        return user
                .getWallets()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(""));
    }

    private List<Expense> getExpensesFromWallet(User user, Integer id, TimePeriod timePeriod) {
        Wallet wallet = getWallet(id, user);
        return wallet
                .getExpenses()
                .stream()
                .filter(expense -> timePeriod.containsDate(expense.getDate()))
                .collect(toList());
    }

    private List<Expense> getAllExpenses(User user, TimePeriod timePeriod) {
        return user
                .getWallets()
                .stream()
                .map(Wallet::getExpenses)
                .flatMap(List::stream)
                .filter(expense -> timePeriod.containsDate(expense.getDate()))
                .collect(toList());
    }
}
