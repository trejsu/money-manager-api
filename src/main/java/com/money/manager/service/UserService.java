package com.money.manager.service;

import com.money.manager.api.UserController;
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
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.rmi.CORBA.Tie;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


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
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        try {
            PropertyUtils.setProperty(user, field, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BadRequestException("Requested field cannot be updated. Validate entered data and try again.");
        }
        userDao.update(user);
    }

    public List<WalletDto> getWallets(String login) {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        List<WalletDto> wallets = new LinkedList<>(singletonList(walletFactory.getSummaryWallet(user)));
        wallets.addAll(walletDao
                .getAllFromUser(user)
                .stream()
                .map(WalletDto::fromWallet)
                .collect(toList()));
        return wallets;
    }

    public void addWallet(String login, WalletDto walletDto) {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        walletDao.addToUser(walletDto.toWallet(), user);
    }

    public Summary getSummary(String login, Integer id, TimePeriod timePeriod) {
        final List<Expense> expenses =
                walletDao.getExpensesByWalletAndTimePeriod(login, id, timePeriod, null, null);
        return calculateSummary(expenses);
    }

    public List<Expense> getExpenses(String login, Integer id, TimePeriod timePeriod, Integer limit, String sort) {
        return walletDao.getExpensesByWalletAndTimePeriod(login, id, timePeriod, limit, sort);
    }

    public Expense getHighestExpense(String login, Integer id, TimePeriod timePeriod) {
        return walletDao.getHighestExpenseByWalletAndTimePeriod(login, id, timePeriod);
    }

    public void addExpense(String login, Integer id, ExpenseDto expense) {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        Wallet wallet = user
                .getWallets()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(""));
        expenseDao.addToUserAndWallet(user, wallet, expense.toExpense());
    }

    public Map<String, BigDecimal> getCountedCategories(String login, Integer id, TimePeriod timePeriod) {
        return walletDao.getCountedCategoriesByWalletAndTimePeriod(login, id, timePeriod);
    }

    public List<BudgetOutputDto> getBudgets(String login, TimePeriod start, TimePeriod end) {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        final List<Budget> budgets = budgetDao.getFromUserAndTimePeriod(user, start, end);
        return budgets
                .stream()
                .map(budget -> BudgetOutputDto.fromBudget(budget, calculateCurrent(login, budget)))
                .collect(toList());
    }

    public void addBudget(String login, Budget budget) {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
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
}
