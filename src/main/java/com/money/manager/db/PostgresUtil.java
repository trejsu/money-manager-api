package com.money.manager.db;

import com.money.manager.model.Budget;
import com.money.manager.model.Category;
import com.money.manager.model.Expense;
import com.money.manager.model.Saving;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Optional;
import java.util.function.Function;

public class PostgresUtil {

    private static SessionFactory sessionFactory;

    private String url;
    private String username;
    private String password;

    public PostgresUtil(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private void createSessionFactory() {
        sessionFactory =
                new Configuration()
                        .configure()
                        .addAnnotatedClass(Budget.class)
                        .addAnnotatedClass(Category.class)
                        .addAnnotatedClass(Expense.class)
                        .addAnnotatedClass(Saving.class)
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Wallet.class)
                        .setProperty("hibernate.connection.url", url)
                        .setProperty("hibernate.connection.username", username)
                        .setProperty("hibernate.connection.password", password)
                        .buildSessionFactory();
    }

    private SessionFactory getSessionFactory() {
        if (sessionFactory == null) createSessionFactory();
        return sessionFactory;
    }

    public static void stop() {
        sessionFactory.close();
    }

    public <T>T executeQuery(Function<Session, T> query) {
        Transaction transaction = null;
        T returned;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            returned = query.apply(session);
            transaction.commit();
        } catch (HibernateException e) {
            Optional.ofNullable(transaction).ifPresent(Transaction::rollback);
            throw e;
        }
        return returned;
    }
}
