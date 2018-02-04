package com.money.manager.api;

import com.money.manager.dto.UserDto;
import com.money.manager.dto.WalletDto;
import com.money.manager.exception.UpdateFieldException;
import com.money.manager.exception.UserNotFoundException;
import com.money.manager.model.money.Money;
import com.money.manager.service.UserService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String USERS_URL = "/resources/users";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    public void shouldReturnListOfUsers() {
        List<UserDto> users = getSampleUsers();
        when(userService.getUsers()).thenReturn(users);

        final ResultActions response = mockMvc.perform(get(USERS_URL));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].admin", containsInAnyOrder(true, false, false)))
                .andExpect(jsonPath("$[*].firstName", containsInAnyOrder("Cody", "Taylor", "Clarence")))
                .andExpect(jsonPath("$[*].lastName", containsInAnyOrder("Willis", "Long", "Graves")))
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("cody.willis54", "taylor.long48", "clarence.graves26")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnNoContentWhenUpdatingUser() {
        final String url = USERS_URL + "/cody.willis54";

        final ResultActions response = mockMvc.perform(put(url)
                        .contentType(APPLICATION_JSON)
                        .content("{}")
                        .param("field", "firstName"));

        response.andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void shouldReturnNotFoundWhenUpdatingNotExistingUser() {
        String login = "cody.willis54";
        final String url = USERS_URL + "/" + login;
        doThrow(new UserNotFoundException(login)).when(userService).updateUser(any(), any(), any());

        final ResultActions response = mockMvc.perform(put(url)
                .contentType(APPLICATION_JSON)
                .content("{}")
                .param("field", "firstName"));

        response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.problem", equalTo("User with login " + login + " was not found.")))
                .andExpect(jsonPath("$.solution", equalTo("Check if provided login is correct and try again.")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnBadRequestWhenUpdatingNotExistingProperty() {
        final String problem = "Requested field cannot be updated.";
        final String solution = "Validate entered data and try again.";
        doThrow(new UpdateFieldException(problem, solution)).when(userService).updateUser(any(), any(), any());

        final ResultActions response = mockMvc.perform(put(USERS_URL + "/someLogin")
                .contentType(APPLICATION_JSON)
                .content("{}")
                .param("field", "firstName"));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problem", equalTo(problem)))
                .andExpect(jsonPath("$.solution", equalTo(solution)));
    }

    @Test
    @SneakyThrows
    public void shouldReturnBadRequestWhenFieldParamIsMissing() {
        final String url = USERS_URL + "/someLogin";

        final ResultActions response = mockMvc.perform(put(url)
                .contentType(APPLICATION_JSON)
                .content("{}"));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void shouldReturnListOfWallets() {
        String login = "someLogin";
        final String url = USERS_URL + "/" + login + "/wallets";
        List<WalletDto> wallets = getSampleWallets();
        when(userService.getWallets(login)).thenReturn(wallets);

        final ResultActions response = mockMvc.perform(get(url));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2, 666, 96)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("wallet", "some wallet", "awesome account", "pocket")))
                .andExpect(jsonPath("$[*].amount.amount", containsInAnyOrder(10, 56.66, 37.99, 2222)))
                .andExpect(jsonPath("$[*].amount.currency", containsInAnyOrder("PLN", "PLN", "USD", "GBP")));
    }

    /*

    @GetMapping(value = "/{login}/wallets", produces = APPLICATION_JSON_VALUE)
    public List<WalletDto> getWallets(@PathVariable("login") String login) {
        return userService.getWallets(login);
    }

    @PostMapping(value = "/{login}/wallets", consumes = APPLICATION_JSON_VALUE)
    public Integer createWallet(@PathVariable("login") String login, @RequestBody @Valid WalletDto walletDto) {
        return userService.addWallet(login, walletDto);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSummary(@PathVariable("login") String login,
                                        @PathVariable("id") Integer id,
                                        @RequestParam(name = "start", required = false) String start,
                                        @RequestParam(name = "end", required = false) String end) {
        return getResponseWithErrorHandling(() -> {
            final Summary summary = userService.getSummary(login, id, new DateRange(start, end));
            return ResponseEntity.ok(summary);
        });
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getExpenses(@PathVariable("login") String login,
                                         @PathVariable("id") Integer id,
                                         @RequestParam(name = "start", required = false) String start,
                                         @RequestParam(name = "end", required = false) String end) {
        return getResponseWithErrorHandling(() -> {
            final DateRange dateRange = new DateRange(start, end);
            final List<Expense> expenses = userService.getExpenses(login, id, dateRange);
            return ResponseEntity.ok(expenses);
        });
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest_expense", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHighestExpense(@PathVariable("login") String login,
                                               @PathVariable("id") Integer id,
                                               @RequestParam(name = "start", required = false) String start,
                                               @RequestParam(name = "end", required = false) String end) {
        return getResponseWithErrorHandling(() -> {
            final DateRange dateRange = new DateRange(start, end);
            final Expense highestExpense = userService.getHighestExpense(login, id, dateRange);
            return ResponseEntity.ok(highestExpense);
        });
    }

    @SneakyThrows
    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createExpense(@PathVariable("login") String login,
                                           @PathVariable("id") Integer id,
                                           @RequestBody @Valid ExpenseInputDto expense) {
        return getResponseWithErrorHandling(() -> {
            final Integer expenseId = userService.addExpense(login, id, expense);
            final String location = "/" + login + "/wallets/" + id + "/expenses/" + expenseId;
            return ResponseEntity.created(URI.create(location)).build();
        });
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping(value = "/{login}/wallets/{wallet_id}/expenses/{expense_id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("login") String login,
                                           @PathVariable("wallet_id") Integer wallet_id,
                                           @PathVariable("expense_id") Integer expense_id) {
        return getResponseWithErrorHandling(() -> {
            userService.deleteExpense(login, wallet_id, expense_id);
            return ResponseEntity.noContent().build();
        });
    }

    @GetMapping(value = "/{login}/wallets/{id}/counted_categories", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCountedCategories(@PathVariable("login") String login,
                                                  @PathVariable("id") Integer id,
                                                  @RequestParam(name = "start", required = false) String start,
                                                  @RequestParam(name = "end", required = false) String end) {
        return getResponseWithErrorHandling(() -> {
            final DateRange dateRange = new DateRange(start, end);
            final Map<String, BigDecimal> countedCategories = userService.getCountedCategories(login, id, dateRange);
            return ResponseEntity.ok(countedCategories);
        });
    }

    @GetMapping(value = "/{login}/budgets", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBudgets(@PathVariable("login") String login,
                                        @RequestParam(name = "start_min", required = false) String startMin,
                                        @RequestParam(name = "start_max", required = false) String startMax,
                                        @RequestParam(name = "end_min", required = false) String endMin,
                                        @RequestParam(name = "end_max", required = false) String endMax) {
        return getResponseWithErrorHandling(() -> {
            final DateRange start = new DateRange(startMin, startMax);
            final DateRange end = new DateRange(endMin, endMax);
            final List<BudgetOutputDto> budgets = userService.getBudgets(login, start, end);
            return ResponseEntity.ok(budgets);
        });
    }

    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBudget(@PathVariable("login") String login, @Valid @RequestBody BudgetInputDto budget) {
        return getResponseWithErrorHandling(() -> {
            final Integer budgetId = userService.addBudget(login, budget.toBudget());
            final String location = "/" + login + "/budgets/" + budgetId;
            return ResponseEntity.created(URI.create(location)).build();
        });
    }

    private ResponseEntity<?> getResponseWithErrorHandling(Supplier<ResponseEntity<?>> getResponse) {
        try {
            return getResponse.get();
        } catch (ErrorResponseException e) {
            return e.getResponseEntity();
        }
    }
     */
    private List<UserDto> getSampleUsers() {
        return asList(
                UserDto.builder().admin(true).firstName("Cody").lastName("Willis").login("cody.willis54").build(),
                UserDto.builder().admin(false).firstName("Taylor").lastName("Long").login("taylor.long48").build(),
                UserDto.builder().admin(false).firstName("Clarence").lastName("Graves").login("clarence.graves26").build());
    }

    private List<WalletDto> getSampleWallets() {
        return asList(
                WalletDto.builder().amount(new Money(BigDecimal.valueOf(10), "PLN")).id(1).name("wallet").build(),
                WalletDto.builder().amount(new Money(BigDecimal.valueOf(56.66), "USD")).id(2).name("some wallet").build(),
                WalletDto.builder().amount(new Money(BigDecimal.valueOf(37.99), "PLN")).id(666).name("awesome account").build(),
                WalletDto.builder().amount(new Money(BigDecimal.valueOf(2222), "GBP")).id(96).name("pocket").build()
        );
    }
}
