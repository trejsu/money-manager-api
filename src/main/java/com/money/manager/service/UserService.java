package com.money.manager.service;

import com.money.manager.db.dao.BudgetDao;
import com.money.manager.db.dao.ExpenseDao;
import com.money.manager.db.dao.UserDao;
import com.money.manager.db.dao.WalletDao;
import com.money.manager.dto.BudgetOutputDto;
import com.money.manager.dto.ExpenseInputDto;
import com.money.manager.dto.ExpenseOutputDto;
import com.money.manager.exception.ExpenseNotFoundException;
import com.money.manager.exception.UpdateFieldException;
import com.money.manager.model.money.Money;
import com.money.manager.dto.Summary;
import com.money.manager.model.DateRange;
import com.money.manager.dto.UserDto;
import com.money.manager.dto.WalletDto;
import com.money.manager.exception.UserNotFoundException;
import com.money.manager.exception.WalletNotFoundException;
import com.money.manager.model.Budget;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.money.manager.dto.ExpenseOutputDto.fromExpense;
import static com.money.manager.service.Predicates.containsExpenseWithId;
import static com.money.manager.service.Predicates.hasId;
import static com.money.manager.service.Predicates.isEligibleExpense;
import static com.money.manager.service.Predicates.isIn;
import static com.money.manager.service.Predicates.isIncludedIn;
import static com.money.manager.service.Predicates.isNotProfit;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Service
public class UserService {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;
    private final static String SUMMARY_WALLET_NAME = "wszystkie";

    private final UserDao userDao;
    private final WalletDao walletDao;
    private final BudgetDao budgetDao;
    private final ExpenseDao expenseDao;

    @Autowired
    public UserService(UserDao userDao, WalletDao walletDao, BudgetDao budgetDao, ExpenseDao expenseDao) {
        this.userDao = userDao;
        this.walletDao = walletDao;
        this.budgetDao = budgetDao;
        this.expenseDao = expenseDao;
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
            throw new UpdateFieldException("Requested field cannot be updated.", "Validate entered data and try again.");
        }
        userDao.update(user);
    }

    public List<WalletDto> getWallets(String login) {
        User user = getUser(login);
        List<WalletDto> wallets = new ArrayList<>();
        wallets.add(getSummaryWallet(user));
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

    public Summary getSummary(String login, Integer id, DateRange dateRange) {
        return calculateSummary(getExpenses(login, id, dateRange));
    }

    public List<ExpenseOutputDto> getExpenses(String login, Integer id, DateRange dateRange) {
        User user = getUser(login);
        return id == 0 ? getAllExpenses(user, dateRange) : getExpensesFromWallet(user, id, dateRange);
    }

    public ExpenseOutputDto getHighestExpense(String login, Integer id, DateRange dateRange) {
        List<ExpenseOutputDto> expenses = getExpenses(login, id, dateRange);
        return expenses
                .stream()
                .filter(isNotProfit)
                .max(comparing(ExpenseOutputDto::getMoney))
                .orElse(null);
    }

    public Integer addExpense(String login, Integer id, ExpenseInputDto expenseInputDto) {
        User user = getUser(login);
        Wallet wallet = getWallet(id, user);
        Expense expense = expenseInputDto.toExpense();
        expense.setDate(LocalDate.now().format(DATE_TIME_FORMATTER));
        Integer expenseId = expenseDao.add(expense);
        wallet.getExpenses().add(expense);
        Money newWalletAmount = getNewAmountAfterAdd(expenseInputDto, wallet);
        wallet.setAmount(newWalletAmount);
        walletDao.update(wallet);
        return expenseId;
    }

    public Map<String, BigDecimal> getCountedCategories(String login, Integer id, DateRange dateRange) {
        final List<ExpenseOutputDto> expenses = getExpenses(login, id, dateRange);
        return expenses
                .stream()
                .filter(isIn(dateRange).and(isEligibleExpense))
                .collect(groupingBy(expense -> expense.getCategory().getName()))
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().stream().map(ExpenseOutputDto::getMoney).reduce(Money.ZERO, Money::add).getAmount()));
    }

    public List<BudgetOutputDto> getBudgets(String login, DateRange start, DateRange end) {
        User user = getUser(login);
        List<ExpenseOutputDto> expenses = getAllExpenses(user, new DateRange(start.getStart(), end.getEnd()));
        return user
                .getBudgets()
                .stream()
                .filter(isIn(start, end))
                .map(budget -> BudgetOutputDto.fromBudget(budget, calculateCurrent(budget, expenses)))
                .collect(toList());
    }

    public Integer addBudget(String login, Budget budget) {
        User user = getUser(login);
        user.getBudgets().add(budget);
        Integer id = budgetDao.add(budget);
        userDao.update(user);
        return id;
    }

    public void deleteExpense(String login, Integer walletId, Integer expenseId) {
        User user = getUser(login);
        Wallet wallet = walletId == 0 ? getWalletByExpenseId(expenseId, user) : getWallet(walletId, user);
        final List<Expense> expenses = wallet.getExpenses();
        Expense toDelete = expenses.stream().filter(hasId(expenseId)).findFirst().orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        expenses.remove(toDelete);
        Money newWalletAmount = getNewAmountAfterExpenseDelete(fromExpense(toDelete), wallet);
        wallet.setAmount(newWalletAmount);
        walletDao.update(wallet);
    }

    private Wallet getWalletByExpenseId(Integer expenseId, User user) {
        return user.getWallets()
                .stream()
                .filter(containsExpenseWithId(expenseId))
                .findFirst()
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
    }

    private Money getNewAmountAfterExpenseDelete(ExpenseOutputDto toDelete, Wallet wallet) {
        Money toSub = toDelete.getMoney();
        Money current = wallet.getAmount();
        return toDelete.getCategory().getProfit() ? current.substract(toSub) : current.add(toSub);
    }

    private Money getNewAmountAfterAdd(ExpenseInputDto expense, Wallet wallet) {
        Money toAdd = expense.getMoney();
        Money current = wallet.getAmount();
        return expense.getCategory().getProfit() ? current.add(toAdd) : current.substract(toAdd);
    }

    private Summary calculateSummary(List<ExpenseOutputDto> expenses) {
        Money inflow = Money.ZERO;
        Money outflow = Money.ZERO;
        for (ExpenseOutputDto expense : expenses) {
            Money money = expense.getMoney();
            if (expense.getCategory().getProfit()) {
                inflow = inflow.add(money);
            } else {
                outflow = outflow.add(money);
            }
        }
        return new Summary(inflow, outflow);
    }

    private Money calculateCurrent(Budget budget, List<ExpenseOutputDto> expenses) {
        return expenses
                .stream()
                .filter(isIncludedIn(budget))
                .map(ExpenseOutputDto::getMoney)
                .reduce(Money.ZERO, Money::add);
    }

    private User getUser(String login) {
        return userDao.get(login).orElseThrow(() -> new UserNotFoundException(login));
    }

    private Wallet getWallet(Integer id, User user) {
        return user
                .getWallets()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(id));
    }

    private List<ExpenseOutputDto> getExpensesFromWallet(User user, Integer id, DateRange dateRange) {
        Wallet wallet = getWallet(id, user);
        return wallet
                .getExpenses()
                .stream()
                .map(ExpenseOutputDto::fromExpense)
                .filter(isIn(dateRange))
                .collect(toList());
    }

    private List<ExpenseOutputDto> getAllExpenses(User user, DateRange dateRange) {
        return user
                .getWallets()
                .stream()
                .map(Wallet::getExpenses)
                .flatMap(List::stream)
                .map(ExpenseOutputDto::fromExpense)
                .filter(isIn(dateRange))
                .collect(toList());
    }

    private WalletDto getSummaryWallet(User user) {
        Money money = user
                .getWallets()
                .stream()
                .map(Wallet::getAmount)
                .reduce(Money.ZERO, Money::add);
        return WalletDto.builder().id(0).money(money).name(SUMMARY_WALLET_NAME).build();
    }


}
