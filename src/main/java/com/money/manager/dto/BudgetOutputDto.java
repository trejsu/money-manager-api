package com.money.manager.dto;

import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BudgetOutputDto {
    private Integer id;
    private Category category;
    private BigDecimal total;
    private BigDecimal current;
    private TimePeriod timePeriod;

    public static BudgetOutputDto fromBudget(Budget budget, BigDecimal current) {
        return builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .total(budget.getTotal())
                .current(current)
                .timePeriod(new TimePeriod(budget.getStart(), budget.getEnd()))
                .build();
    }
}
