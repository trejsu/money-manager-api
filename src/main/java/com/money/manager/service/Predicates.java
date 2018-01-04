package com.money.manager.service;

import com.money.manager.dto.ExpenseOutputDto;
import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;

import java.util.function.Predicate;

// todo: move it to more suitable package
class Predicates {

    private final static String TRANSFER_CATEGORY = "transfer";

    final static Predicate<ExpenseOutputDto> isProfit = expense -> expense.getCategory().isProfit();
    final static Predicate<ExpenseOutputDto> isNotProfit = isProfit.negate();
    final static Predicate<ExpenseOutputDto> isNotTransfer = expense -> !expense.getCategory().getName().equals(TRANSFER_CATEGORY);
    final static Predicate<ExpenseOutputDto> isEligibleExpense = isNotTransfer.and(isNotProfit);

    static Predicate<ExpenseOutputDto> isIn(TimePeriod timePeriod) {
        return expense -> timePeriod.containsDate(expense.getDate());
    }

    static Predicate<Budget> isIn(TimePeriod start, TimePeriod end) {
        return budget -> start.containsDate(budget.getStart()) && end.containsDate(budget.getEnd());
    }

    static Predicate<ExpenseOutputDto> isIncludedIn(Budget budget) {
        TimePeriod timePeriod = new TimePeriod(budget.getStart(), budget.getEnd());
        return matchesCategoryOf(budget).and(isIn(timePeriod));
    }

    static Predicate<ExpenseOutputDto> matchesCategoryOf(Budget budget) {
        return expense -> expense.getCategory().equals(budget.getCategory());
    }
}
