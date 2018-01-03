package com.money.manager.dto;

import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseInputDto {
    private String message;
    private Money money;
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
