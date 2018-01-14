package com.money.manager.dto;

import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseInputDto {

    private String message;

    @NotNull
    @Valid
    private Money money;

    @NotNull
    @Valid
    private Category category;

    public Expense toExpense() {
        return Expense.builder()
                .amount(money.getAmount())
                .currency(money.getCurrency().getCurrencyCode())
                .category(category)
                .message(message)
                .build();
    }
}
