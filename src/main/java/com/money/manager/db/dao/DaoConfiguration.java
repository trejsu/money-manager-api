package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfiguration {

    @Bean
    public UserDao userDao(PostgresUtil postgres) {
        return new PostgresUserDao(postgres);
    }

    @Bean
    public BudgetDao budgetDao(PostgresUtil postgres) {
        return new PostgresBudgetDao(userDao(postgres), postgres);
    }

    @Bean
    public ExpenseDao expenseDao(PostgresUtil postgres) {
        return new PostgresExpenseDao(postgres);
    }

}
