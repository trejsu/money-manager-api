package com.money.manager.api;

import com.money.manager.dao.BudgetDao;
import com.money.manager.dao.ExpenseDao;
import com.money.manager.dao.UserDao;
import com.money.manager.dao.WalletDao;
import com.money.manager.dto.ExpenseDto;
import com.money.manager.dto.WalletDto;
import com.money.manager.dto.UserDto;
import com.money.manager.dto.Summary;
import com.money.manager.dto.TimePeriod;
import com.money.manager.exception.UserNotFoundException;
import com.money.manager.exception.WalletNotFoundException;
import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.exception.CustomException;
import com.money.manager.factory.WalletFactory;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.util.SummaryCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
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
    public List<UserDto> getUsers() throws CustomException {
        return userDao
                .findAll()
                .stream()
                .map(UserDto::fromUser)
                .collect(toList());
    }

    @PutMapping(value = "/{login}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public <T> void updateUser(@PathVariable("login") String login, @RequestParam("field") String field, @RequestBody LinkedHashMap<String, T> value) throws CustomException {
        userDao.updateField(login, field, value.get(field));
    }

    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public List<WalletDto> getWallets(@PathVariable("login") String login) throws CustomException {
        // todo: move it from here to service
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        List<WalletDto> wallets = new LinkedList<>(singletonList(WalletFactory.getSummaryWallet(user)));
        wallets.addAll(walletDao
                .getAllFromUser(user)
                .stream()
                .map(WalletDto::fromWallet)
                .collect(toList()));
        return wallets;
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public void createWallet(@PathVariable("login") String login, @RequestBody WalletDto walletDto) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        walletDao.addToUser(walletDto.toWallet(), user);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public Summary getSummary(
            @PathVariable("login") String login,
            @PathVariable("id") Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end
    ) throws CustomException {
        final List<Expense> expenses =
                walletDao.getExpensesByWalletAndTimePeriod(login, id, new TimePeriod(start, end), null, null);
        return SummaryCalculator.calculateSummary(expenses);
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
            @RequestBody ExpenseDto expense
    ) throws CustomException {
        // todo: move to service
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        Wallet wallet = user
                .getWallets()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(""));
        expenseDao.addToUserAndWallet(user, wallet, expense.toExpense());
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
    public List<BudgetOutputDto> getBudgets(
            @PathVariable("login") String login,
            @RequestParam(name = "start_min", required = false) String startMin,
            @RequestParam(name = "start_max", required = false) String startMax,
            @RequestParam(name = "end_min", required = false) String endMin,
            @RequestParam(name = "end_max", required = false) String endMax
    ) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        final List<Budget> budgets = budgetDao.getFromUserAndTimePeriod(user, new TimePeriod(startMin, startMax), new TimePeriod(endMin, endMax));
        return budgets
                .stream()
                .map(budget -> BudgetOutputDto.fromBudget(budget, calculateCurrent(login, budget)))
                .collect(toList());
    }


    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public void createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) throws CustomException {
        User user = userDao.get(login).orElseThrow(() -> new UserNotFoundException(""));
        budgetDao.addToUser(budget.toBudget(), user);
    }

    // todo: move to service
    @SneakyThrows
    private BigDecimal calculateCurrent(String login, Budget budget) {
        return expenseDao.getSummaryByUserTimePeriodAndCategory(
                login,
                new TimePeriod(budget.getStart(), budget.getEnd()),
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

        public Budget toBudget() {
            return Budget.builder()
                    .category(category)
                    .total(total)
                    .start(timePeriod.getStart())
                    .end(timePeriod.getEnd())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    static class BudgetOutputDto {
        private Category category;
        private BigDecimal total;
        private BigDecimal current;
        private TimePeriod timePeriod;

        public static BudgetOutputDto fromBudget(Budget budget, BigDecimal current) {
            return builder()
                    .category(budget.getCategory())
                    .total(budget.getTotal())
                    .current(current)
                    .timePeriod(new TimePeriod(budget.getStart(), budget.getEnd()))
                    .build();
        }
    }
}
