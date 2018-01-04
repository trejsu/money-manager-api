package com.money.manager.service;

import com.money.manager.db.dao.BudgetDao;
import com.money.manager.db.dao.ExpenseDao;
import com.money.manager.db.dao.UserDao;
import com.money.manager.db.dao.WalletDao;
import com.money.manager.dto.BudgetOutputDto;
import com.money.manager.dto.ExpenseInputDto;
import com.money.manager.dto.ExpenseOutputDto;
import com.money.manager.dto.Money;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.money.manager.service.Predicates.isEligibleExpense;
import static com.money.manager.service.Predicates.isIn;
import static com.money.manager.service.Predicates.isIncludedIn;
import static com.money.manager.service.Predicates.isNotProfit;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Service
public class UserService {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;

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

    public List<ExpenseOutputDto> getExpenses(String login, Integer id, TimePeriod timePeriod) {
        User user = getUser(login);
        return id == 0 ? getAllExpenses(user, timePeriod) : getExpensesFromWallet(user, id, timePeriod);
    }

    public ExpenseOutputDto getHighestExpense(String login, Integer id, TimePeriod timePeriod) {
        List<ExpenseOutputDto> expenses = getExpenses(login, id, timePeriod);
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
        updateAmount(wallet, expense);
        walletDao.update(wallet);
        return expenseId;
    }

    public Map<String, BigDecimal> getCountedCategories(String login, Integer id, TimePeriod timePeriod) {
        final List<ExpenseOutputDto> expenses = getExpenses(login, id, timePeriod);
        return expenses
                .stream()
                .filter(isIn(timePeriod).and(isEligibleExpense))
                .collect(groupingBy(expense -> expense.getCategory().getName()))
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().stream().map(ExpenseOutputDto::getMoney).reduce(Money.zero(), Money::add).getAmount()));
    }

    public List<BudgetOutputDto> getBudgets(String login, TimePeriod start, TimePeriod end) {
        User user = getUser(login);
        List <ExpenseOutputDto> expenses = getAllExpenses(user, new TimePeriod(start.getStart(), end.getEnd()));
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

    private Summary calculateSummary(List<ExpenseOutputDto> expenses) {
        Money inflow = Money.zero();
        Money outflow = Money.zero();
        for (ExpenseOutputDto expense : expenses) {
            Money money = expense.getMoney();
            if (expense.getCategory().isProfit()) {
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
                .reduce(Money.zero(), Money::add);
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

    private List<ExpenseOutputDto> getExpensesFromWallet(User user, Integer id, TimePeriod timePeriod) {
        Wallet wallet = getWallet(id, user);
        return wallet
                .getExpenses()
                .stream()
                .map(ExpenseOutputDto::fromExpense)
                .filter(isIn(timePeriod))
                .collect(toList());
    }

    private List<ExpenseOutputDto> getAllExpenses(User user, TimePeriod timePeriod) {
        return user
                .getWallets()
                .stream()
                .map(Wallet::getExpenses)
                .flatMap(List::stream)
                .map(ExpenseOutputDto::fromExpense)
                .filter(isIn(timePeriod))
                .collect(toList());
    }

    private void updateAmount(Wallet wallet, Expense expense) {
        BigDecimal amount = expense.getAmount();
        if (!expense.getCategory().isProfit()) {
            amount = amount.negate();
        }
        wallet.setAmount(wallet.getAmount().add(amount));
    }
}
