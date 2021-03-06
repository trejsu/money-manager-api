package com.money.manager.api;

import com.money.manager.dto.BudgetOutputDto;
import com.money.manager.dto.ExpenseInputDto;
import com.money.manager.model.Expense;
import com.money.manager.model.money.Money;
import com.money.manager.dto.WalletDto;
import com.money.manager.dto.UserDto;
import com.money.manager.dto.Summary;
import com.money.manager.model.DateRange;
import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.service.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/resources/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping(value = "/{login}", consumes = APPLICATION_JSON_VALUE)
    public <T> ResponseEntity<?> updateUser(@PathVariable("login") String login,
                                            @RequestParam("field") String field,
                                            @RequestBody LinkedHashMap<String, T> value) {
        userService.updateUser(login, field, value);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WalletDto>> getWallets(@PathVariable("login") String login) {
        final List<WalletDto> wallets = userService.getWallets(login);
        return ResponseEntity.ok(wallets);
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWallet(@PathVariable("login") String login, @RequestBody @Valid WalletDto walletDto) {
        final Integer id = userService.addWallet(login, walletDto);
        return ResponseEntity.created(URI.create("")).body(id);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSummary(@PathVariable("login") String login,
                                        @PathVariable("id") Integer id,
                                        @RequestParam(name = "start", required = false) String start,
                                        @RequestParam(name = "end", required = false) String end) {
        final Summary summary = userService.getSummary(login, id, new DateRange(start, end));
        return ResponseEntity.ok(summary);
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpenses(@PathVariable("login") String login,
                                         @PathVariable("id") Integer id,
                                         @RequestParam(name = "start", required = false) String start,
                                         @RequestParam(name = "end", required = false) String end) {
        final DateRange dateRange = new DateRange(start, end);
        final List<Expense> expenses = userService.getExpenses(login, id, dateRange);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest_expense", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHighestExpense(@PathVariable("login") String login,
                                               @PathVariable("id") Integer id,
                                               @RequestParam(name = "start", required = false) String start,
                                               @RequestParam(name = "end", required = false) String end) {
        final DateRange dateRange = new DateRange(start, end);
        final Expense highestExpense = userService.getHighestExpense(login, id, dateRange);
        return ResponseEntity.ok(highestExpense);
    }

    @SneakyThrows
    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExpense(@PathVariable("login") String login,
                                           @PathVariable("id") Integer id,
                                           @RequestBody @Valid ExpenseInputDto expense) {
        final Integer expenseId = userService.addExpense(login, id, expense);
        final String location = "/" + login + "/wallets/" + id + "/expenses/" + expenseId;
        return ResponseEntity.created(URI.create(location)).build();
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping(value = "/{login}/wallets/{wallet_id}/expenses/{expense_id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("login") String login,
                                           @PathVariable("wallet_id") Integer wallet_id,
                                           @PathVariable("expense_id") Integer expense_id) {
        userService.deleteExpense(login, wallet_id, expense_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{login}/wallets/{id}/counted_categories", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCountedCategories(@PathVariable("login") String login,
                                                  @PathVariable("id") Integer id,
                                                  @RequestParam(name = "start", required = false) String start,
                                                  @RequestParam(name = "end", required = false) String end) {
        final DateRange dateRange = new DateRange(start, end);
        final Map<String, BigDecimal> countedCategories = userService.getCountedCategories(login, id, dateRange);
        return ResponseEntity.ok(countedCategories);
    }

    @GetMapping(value = "/{login}/budgets", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBudgets(@PathVariable("login") String login,
                                        @RequestParam(name = "start_min", required = false) String startMin,
                                        @RequestParam(name = "start_max", required = false) String startMax,
                                        @RequestParam(name = "end_min", required = false) String endMin,
                                        @RequestParam(name = "end_max", required = false) String endMax) {
        final DateRange start = new DateRange(startMin, startMax);
        final DateRange end = new DateRange(endMin, endMax);
        final List<BudgetOutputDto> budgets = userService.getBudgets(login, start, end);
        return ResponseEntity.ok(budgets);
    }

    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) {
        final Integer budgetId = userService.addBudget(login, budget.toBudget());
        final String location = "/" + login + "/budgets/" + budgetId;
        return ResponseEntity.created(URI.create(location)).build();
    }

    @Data
    @NoArgsConstructor
    static class BudgetInputDto {

        @NotNull
        @Valid
        private Category category;

        @NotNull
        @Valid
        private Money total;

        @NotNull
        @Valid
        private DateRange dateRange;

        Budget toBudget() {
            return Budget.builder()
                    .category(category)
                    .total(total)
                    .dateRange(dateRange)
                    .build();
        }
    }
}
