package com.money.manager.api;

import com.money.manager.dto.BudgetOutputDto;
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
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.service.UserService;
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
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
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

    @PutMapping(value = "/{login}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public <T> void updateUser(@PathVariable("login") String login, @RequestParam("field") String field, @RequestBody LinkedHashMap<String, T> value) {
        userService.updateUser(login, field, value);
    }

    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public List<WalletDto> getWallets(@PathVariable("login") String login) {
        return userService.getWallets(login);
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public void createWallet(@PathVariable("login") String login, @RequestBody WalletDto walletDto) {
        userService.addWallet(login, walletDto);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public Summary getSummary(@PathVariable("login") String login,
                              @PathVariable("id") Integer id,
                              @RequestParam(name = "start", required = false) String start,
                              @RequestParam(name = "end", required = false) String end) {
        return userService.getSummary(login, id, new TimePeriod(start, end));
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = APPLICATION_JSON_VALUE)
    public List<Expense> getExpenses(@PathVariable("login") String login,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(name = "start", required = false) String start,
                                     @RequestParam(name = "end", required = false) String end,
                                     @RequestParam(name = "limit", required = false) Integer limit,
                                     @RequestParam(name = "sort", required = false) String sort) {
        return userService.getExpenses(login, id, new TimePeriod(start, end), limit, sort);
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest_expense", produces = APPLICATION_JSON_VALUE)
    public Expense getHighestExpense(@PathVariable("login") String login,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(name = "start", required = false) String start,
                                     @RequestParam(name = "end", required = false) String end) {
        return userService.getHighestExpense(login, id, new TimePeriod(start, end));
    }

    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = APPLICATION_JSON_VALUE)
    public void createExpense(@PathVariable("login") String login,
                              @PathVariable("id") Integer id,
                              @RequestBody ExpenseDto expense) {
        userService.addExpense(login, id, expense);
    }

    @GetMapping(value = "/{login}/wallets/{id}/counted_categories", produces = APPLICATION_JSON_VALUE)
    public Map<String, BigDecimal> getCountedCategories(@PathVariable("login") String login,
                                                        @PathVariable("id") Integer id,
                                                        @RequestParam(name = "start", required = false) String start,
                                                        @RequestParam(name = "end", required = false) String end) {
        return userService.getCountedCategories(login, id, new TimePeriod(start, end));
    }

    @GetMapping(value = "/{login}/budgets", produces = APPLICATION_JSON_VALUE)
    public List<BudgetOutputDto> getBudgets(@PathVariable("login") String login,
                                            @RequestParam(name = "start_min", required = false) String startMin,
                                            @RequestParam(name = "start_max", required = false) String startMax,
                                            @RequestParam(name = "end_min", required = false) String endMin,
                                            @RequestParam(name = "end_max", required = false) String endMax) {
        return userService.getBudgets(login, new TimePeriod(startMin, startMax), new TimePeriod(endMin, endMax));
    }


    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public void createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) {
        userService.addBudget(login, budget.toBudget());
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
}
