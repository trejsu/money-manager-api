package com.money.manager.dto;

import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BudgetOutputDto {
    private Integer id;
    private Category category;
    private Money total;
    private Money current;
    private DateRange dateRange;

    public static BudgetOutputDto fromBudget(Budget budget, Money current) {
        return builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .total(new Money(budget.getTotal(), budget.getCurrency()))
                .current(current)
                .dateRange(new DateRange(LocalDate.parse(budget.getStart()), LocalDate.parse(budget.getEnd())))
                .build();
    }
}
