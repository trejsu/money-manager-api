package com.money.manager.dto;

import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseDto {
    private String message;
    private BigDecimal amount;
    private Category category;

    public Expense toExpense() {
        return Expense.builder()
                .amount(amount)
                .category(category)
                .message(message)
                .build();
    }
}
