package com.money.manager.dto;

import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate date;

    public static ExpenseOutputDto fromExpense(Expense expense) {
        return ExpenseOutputDto.builder()
                .id(expense.getId())
                .message(expense.getMessage())
                .money(expense.getAmount())
                .category(expense.getCategory())
                .date(LocalDate.parse(expense.getDate()))
                .build();
    }
}
