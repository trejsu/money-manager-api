package com.money.manager.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfiguration {

    @Bean
    public UserDao userDao() {
        return new HibernateUserDao();
    }

    @Bean
    public BudgetDao budgetDao() {
        return new HibernateBudgetDao();
    }

    @Bean
    public ExpenseDao expenseDao() {
        return new HibernateExpenseDao();
    }
}
