package com.money.manager.util;

import com.money.manager.entity.Budget;
import com.money.manager.entity.Category;
import com.money.manager.entity.Expense;
import com.money.manager.entity.Saving;
import com.money.manager.entity.User;
import com.money.manager.entity.Wallet;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.DatabaseConnectionException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static URI dbUri;

    private static void createSessionFactory() {
        setDBUri();
        sessionFactory =
                new Configuration()
                        .configure()
                        .addAnnotatedClass(Budget.class)
                        .addAnnotatedClass(Category.class)
                        .addAnnotatedClass(Expense.class)
                        .addAnnotatedClass(Saving.class)
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Wallet.class)
                        .setProperty("hibernate.connection.url", getURL())
                        .setProperty("hibernate.connection.username", getUsername())
                        .setProperty("hibernate.connection.password", getPassword())
                        .buildSessionFactory();
    }

    private static void setDBUri() {
        try {
            String databaseUrl = "postgres://postgres:password@localhost:5432/moneymanager";
            dbUri = new URI(databaseUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't connect to database, URI is malformed.");
        }
    }

    private static String getUsername() {
        return dbUri.getUserInfo().split(":")[0];
    }

    private static String getPassword() {
        return dbUri.getUserInfo().split(":")[1];
    }

    private static String getURL() {
        return "jdbc:postgresql://"
                + dbUri.getHost()
                + ':'
                + dbUri.getPort()
                + dbUri.getPath()
                + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) createSessionFactory();
        return sessionFactory;
    }

    public static void stop() {
        sessionFactory.close();
    }

    public static <T>T executeQuery(QueryFunction<Session, T> query, String errorMessage) throws CustomException {
        Transaction transaction = null;
        T returned;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            returned = query.apply(session);
            transaction.commit();
        } catch (HibernateException e) {
            handleHibernateException(transaction, e);
            throw new DatabaseConnectionException(errorMessage);
        }
        return returned;
    }

    private static void handleHibernateException(Transaction transaction, HibernateException e) {
        System.out.println(e.getMessage());
        if (transaction != null) transaction.rollback();
        e.printStackTrace();
    }
}
