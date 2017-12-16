package com.money.manager.util;

import com.money.manager.dto.Summary;
import com.money.manager.model.Expense;

import java.math.BigDecimal;
import java.util.List;

public class SummaryCalculator {

    public static Summary calculateSummary(List<Expense> expenses) {
        BigDecimal inflow = new BigDecimal(0);
        BigDecimal outflow = new BigDecimal(0);
        for (Expense expense : expenses) {
            BigDecimal amount = expense.getAmount();
            if (expense.getCategory().isProfit()) {
                inflow = inflow.add(amount);
            } else {
                outflow = outflow.add(amount);
            }
        }
        return new Summary(inflow, outflow);
    }
}
