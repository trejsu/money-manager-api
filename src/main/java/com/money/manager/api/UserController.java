package com.money.manager.api;

import com.money.manager.dto.BudgetOutputDto;
import com.money.manager.dto.ExpenseInputDto;
import com.money.manager.dto.ExpenseOutputDto;
import com.money.manager.model.money.Money;
import com.money.manager.dto.WalletDto;
import com.money.manager.dto.UserDto;
import com.money.manager.dto.Summary;
import com.money.manager.dto.DateRange;
import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.service.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public <T> void updateUser(@PathVariable("login") String login, @RequestParam("field") String field, @RequestBody LinkedHashMap<String, T> value) {
        userService.updateUser(login, field, value);
    }

    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public List<WalletDto> getWallets(@PathVariable("login") String login) {
        return userService.getWallets(login);
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public Integer createWallet(@PathVariable("login") String login, @RequestBody WalletDto walletDto) {
        return userService.addWallet(login, walletDto);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public Summary getSummary(@PathVariable("login") String login,
                              @PathVariable("id") Integer id,
                              @RequestParam(name = "start", required = false) String start,
                              @RequestParam(name = "end", required = false) String end) {
        return userService.getSummary(login, id, new DateRange(start, end));
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = APPLICATION_JSON_VALUE)
    public List<ExpenseOutputDto> getExpenses(@PathVariable("login") String login,
                                              @PathVariable("id") Integer id,
                                              @RequestParam(name = "start", required = false) String start,
                                              @RequestParam(name = "end", required = false) String end) {
        return userService.getExpenses(login, id, new DateRange(start, end));
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest_expense", produces = APPLICATION_JSON_VALUE)
    public ExpenseOutputDto getHighestExpense(@PathVariable("login") String login,
                                     @PathVariable("id") Integer id,
                                     @RequestParam(name = "start", required = false) String start,
                                     @RequestParam(name = "end", required = false) String end) {
        return userService.getHighestExpense(login, id, new DateRange(start, end));
    }

    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = APPLICATION_JSON_VALUE)
    public Integer createExpense(@PathVariable("login") String login,
                              @PathVariable("id") Integer id,
                              @RequestBody ExpenseInputDto expense) {
        return userService.addExpense(login, id, expense);
    }

    @DeleteMapping(value = "/{login}/wallets/{wallet_id}/expenses/{expense_id}")
    public void deleteExpense(@PathVariable("login") String login,
                              @PathVariable("wallet_id") Integer wallet_id,
                              @PathVariable("expense_id") Integer expense_id) {
        userService.deleteExpense(login, wallet_id, expense_id);
    }

    @GetMapping(value = "/{login}/wallets/{id}/counted_categories", produces = APPLICATION_JSON_VALUE)
    public Map<String, BigDecimal> getCountedCategories(@PathVariable("login") String login,
                                                        @PathVariable("id") Integer id,
                                                        @RequestParam(name = "start", required = false) String start,
                                                        @RequestParam(name = "end", required = false) String end) {
        return userService.getCountedCategories(login, id, new DateRange(start, end));
    }

    @GetMapping(value = "/{login}/budgets", produces = APPLICATION_JSON_VALUE)
    public List<BudgetOutputDto> getBudgets(@PathVariable("login") String login,
                                            @RequestParam(name = "start_min", required = false) String startMin,
                                            @RequestParam(name = "start_max", required = false) String startMax,
                                            @RequestParam(name = "end_min", required = false) String endMin,
                                            @RequestParam(name = "end_max", required = false) String endMax) {
        return userService.getBudgets(login, new DateRange(startMin, startMax), new DateRange(endMin, endMax));
    }


    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public Integer createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) {
        return userService.addBudget(login, budget.toBudget());
    }

    @Data
    @NoArgsConstructor
    static class BudgetInputDto {
        @NotNull
        private Category category;
        @NotNull
        private Money total;
        @Valid
        @NotNull
        private DateRange dateRange;

        public Budget toBudget() {
            return Budget.builder()
                    .category(category)
                    .total(total.getAmount())
                    .currency(total.getCurrency().getCurrencyCode())
                    .start(dateRange.getStart())
                    .end(dateRange.getEnd())
                    .build();
        }
    }
}
