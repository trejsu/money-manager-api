package com.money.manager.dto;

import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseOutputDto {
    private Integer id;
    private String message;
    private Money money;
    private Category category;

    public static ExpenseOutputDto fromExpense(Expense expense) {
        return ExpenseOutputDto.builder()
                .id(expense.getId())
                .message(expense.getMessage())
                .money(new Money(expense.getAmount(), expense.getCurrency()))
                .message(expense.getMessage())
                .build();
    }
}
