package com.money.manager.service;

import com.money.manager.dto.ExpenseOutputDto;
import com.money.manager.dto.DateRange;
import com.money.manager.model.Budget;
import com.money.manager.model.Expense;
import com.money.manager.model.Wallet;

import java.util.function.Predicate;

// todo: move it to more suitable package
class Predicates {

    private final static String TRANSFER_CATEGORY = "transfer";

    final static Predicate<ExpenseOutputDto> isProfit = expense -> expense.getCategory().isProfit();
    final static Predicate<ExpenseOutputDto> isNotProfit = isProfit.negate();
    final static Predicate<ExpenseOutputDto> isNotTransfer = expense -> !expense.getCategory().getName().equals(TRANSFER_CATEGORY);
    final static Predicate<ExpenseOutputDto> isEligibleExpense = isNotTransfer.and(isNotProfit);

    static Predicate<ExpenseOutputDto> isIn(DateRange dateRange) {
        return expense -> dateRange.containsDate(expense.getDate());
    }

    static Predicate<Budget> isIn(DateRange start, DateRange end) {
        return budget -> start.containsDate(budget.getStart()) && end.containsDate(budget.getEnd());
    }

    static Predicate<ExpenseOutputDto> isIncludedIn(Budget budget) {
        DateRange dateRange = new DateRange(budget.getStart(), budget.getEnd());
        return matchesCategoryOf(budget).and(isIn(dateRange));
    }

    static Predicate<ExpenseOutputDto> matchesCategoryOf(Budget budget) {
        return expense -> expense.getCategory().equals(budget.getCategory());
    }

    static Predicate<Expense> hasId(Integer id) {
        return expense -> expense.getId().equals(id);
    }

    static Predicate<Wallet> containsExpenseWithId(Integer id) {
        return wallet -> wallet.getExpenses().stream().anyMatch(hasId(id));
    }
}
