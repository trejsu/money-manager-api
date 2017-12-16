package com.money.manager.api;

import com.money.manager.dao.BudgetDao;
import com.money.manager.dao.ExpenseDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dao.WalletDao;
import com.money.manager.dto.NoExpensesWallet;
import com.money.manager.dto.NoPasswordUser;
import com.money.manager.dto.Summary;
import com.money.manager.dto.TimePeriod;
import com.money.manager.exception.UserNotFoundException;
import com.money.manager.exception.WalletNotFoundException;
import com.money.manager.factory.UserFactory;
import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.exception.CustomException;
import com.money.manager.factory.WalletFactory;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.util.SummaryCalculator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.money.manager.factory.WalletFactory.getWalletEntityFromNoExpensesWallet;
import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/resources/users")
public class UserController {

    private final UserDao userDao;
    private final WalletDao walletDao;
    private final BudgetDao budgetDao;
    private final ExpenseDao expenseDao;

    @Autowired
    public UserController(UserDao userDao, WalletDao walletDao, BudgetDao budgetDao, ExpenseDao expenseDao) {
        this.userDao = userDao;
        this.walletDao = walletDao;
        this.budgetDao = budgetDao;
        this.expenseDao = expenseDao;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<NoPasswordUser> getUsers() throws CustomException {
        return userDao
                .findAll()
                .stream()
                .map(UserFactory::getNoPasswordUserFromUserEntity)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/{login}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public <T> void updateUser(@PathVariable("login") String login, @RequestParam("field") String field, @RequestBody LinkedHashMap<String, T> value) throws CustomException {
        userDao.updateField(login, field, value.get(field));
    }

    // todo: sorting
    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public List<NoExpensesWallet> getWallets(@PathVariable("login") String login) throws CustomException {
        return getNoExpensesWallets(login);
    }

    // todo: move it from here
    private List<NoExpensesWallet> getNoExpensesWallets(String login) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        List<NoExpensesWallet> wallets = new LinkedList<>(singletonList(WalletFactory.getSummaryWallet(user)));
        wallets.addAll(walletDao
                .getAllFromUser(user)
                .stream()
                .map(WalletFactory::getNoExpensesWalletFromWalletEntity)
                .collect(Collectors.toList()));
        return wallets;
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public void createWallet(@PathVariable("login") String login, @RequestBody NoExpensesWallet noExpensesWallet) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        walletDao.addToUser(getWalletEntityFromNoExpensesWallet(noExpensesWallet), user);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public Summary getThisMonthSummary(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end
    ) throws CustomException {
        final List<Expense> expensesByWalletAndTimePeriod = 
                walletDao.getExpensesByWalletAndTimePeriod(login, id, new TimePeriod(start, end), null, null);
        return SummaryCalculator.calculateSummary(expensesByWalletAndTimePeriod);
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = APPLICATION_JSON_VALUE)
    public List<Expense> getExpenses(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "sort", required = false) String sort
    ) throws CustomException {
        return walletDao.getExpensesByWalletAndTimePeriod(login, id, new TimePeriod(start, end), limit, sort);
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest_expense", produces = APPLICATION_JSON_VALUE)
    public Expense getHighestExpense(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end
    ) throws CustomException {
            TimePeriod timePeriod = new TimePeriod(start, end);
            return walletDao.getHighestExpenseByWalletAndTimePeriod(login, id, timePeriod);
    }

    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = APPLICATION_JSON_VALUE)
    public void createExpense(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestBody Expense expense
    ) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        Wallet wallet = user
                .getWallets()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(""));
        expenseDao.addToUserAndWallet(user, wallet, expense);
    }

    @GetMapping(value = "/{login}/wallets/{id}/counted_categories", produces = APPLICATION_JSON_VALUE)
    public Map<String, BigDecimal> getCountedCategories(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end
    ) throws CustomException {
            TimePeriod timePeriod = new TimePeriod(start, end);
            return walletDao.getCountedCategoriesByWalletAndTimePeriod(login, id, timePeriod);
    }

    @GetMapping(value = "/{login}/budgets", produces = APPLICATION_JSON_VALUE)
    public List<Budget> getBudgets(
            @PathVariable("login") String login,
            @RequestParam(name = "start_min", required = false) String startMin,
            @RequestParam(name = "start_max", required = false) String startMax,
            @RequestParam(name = "end_min", required = false) String endMin,
            @RequestParam(name = "end_max", required = false) String endMax
    ) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        return budgetDao.getFromUserAndTimePeriod(user, new TimePeriod(startMin, startMax), new TimePeriod(endMin, endMax));
    }


    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public void createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        budgetDao.addToUser(budget.toBudget(calculateCurrent(login, budget)), user);
    }

    // todo: move to service
    private BigDecimal calculateCurrent(String login, BudgetInputDto budget) throws CustomException {
        return expenseDao.getSummaryByUserTimePeriodAndCategory(
                login,
                budget.getTimePeriod(),
                budget.getCategory()
        );
    }

    @Data
    @NoArgsConstructor
    static class BudgetInputDto {
        @NotNull
        private Category category;
        @NotNull
        private BigDecimal total;
        @Valid
        @NotNull
        private TimePeriod timePeriod;

        public Budget toBudget(BigDecimal current) {
            return Budget.builder()
                    .category(category)
                    .total(total)
                    .start(timePeriod.getStart())
                    .end(timePeriod.getEnd())
                    .current(current)
                    .build();
        }
    }
}
