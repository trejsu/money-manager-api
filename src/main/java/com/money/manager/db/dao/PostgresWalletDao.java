package com.money.manager.db.dao;

import com.money.manager.db.PostgresUtil;
import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Expense;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingInt;

@Service
public class PostgresWalletDao implements WalletDao {

    private final static int ID_OF_SUMMARY_WALLET = 0;

    private final UserDao userDao;
    private final PostgresUtil postgres;

    @Autowired
    public PostgresWalletDao(UserDao userDao, PostgresUtil postgres) {
        this.userDao = userDao;
        this.postgres = postgres;
    }

    // todo: it fits more to expenses dao
    // todo: user not found
    // todo: wallet not found
    @Override
    public List<Expense> getExpensesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod, Integer limit, String sort) {
        return postgres.executeQuery(session -> {
            String query =
                    "SELECT e FROM User u " +
                    "JOIN u.wallets w " +
                    "JOIN w.expenses e " +
                    getWhereClause(id) +
                    "AND e.date >= :start " +
                    "AND e.date <= :end " +
                    getOrderBy(sort);
            return session
                    .createQuery(query, Expense.class)
                    .setParameter("id", getId(id, login))
                    .setParameter("start", timePeriod.getStart())
                    .setParameter("end" , timePeriod.getEnd())
                    .setMaxResults(getMaxResults(limit))
                    .list();
        });
    }

    @Override
    public Expense getHighestExpenseByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) {
        return postgres.executeQuery(session -> {
            String query =
                    "SELECT e FROM User u " +
                            "JOIN u.wallets w " +
                            "JOIN w.expenses e " +
                            "JOIN e.category c " +
                            getWhereClause(id) +
                            "AND e.date >= :start " +
                            "AND e.date <= :end " +
                            "AND c.profit = FALSE " +
                            "ORDER BY e.amount DESC ";
            try {
                return session
                        .createQuery(query, Expense.class)
                        .setParameter("id", getId(id, login))
                        .setParameter("start", timePeriod.getStart())
                        .setParameter("end" , timePeriod.getEnd())
                        .setMaxResults(1)
                        .getSingleResult();
            } catch(NoResultException e) {
                return null;
            }
        });
    }

    @Override
    public Map<String, BigDecimal> getCountedCategoriesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) {
        return postgres.executeQuery(session -> {
            String queryString =
                "SELECT c.name, sum(e.amount) FROM User u " +
                "JOIN u.wallets w " +
                "JOIN w.expenses e " +
                "JOIN e.category c " +
                getWhereClause(id) +
                "AND e.date >= :start " +
                "AND e.date <= :end " +
                "AND c.profit = FALSE " +
                "AND c.name != :transfer " +
                "GROUP BY c.name " +
                "ORDER BY sum(e.amount) DESC";
            Query query = session
                    .createQuery(queryString)
                    .setParameter("id", getId(id, login))
                    .setParameter("start", timePeriod.getStart())
                    .setParameter("transfer", "transfer")
                    .setParameter("end", timePeriod.getEnd());
            return getMapFromQuery(query.list());
        });
    }

    private Map<String, BigDecimal> getMapFromQuery(List list) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        Iterator itr = list.iterator();
        while(itr.hasNext()){
            Object[] obj = (Object[]) itr.next();
            result.put((String) obj[0], (BigDecimal) obj[1]);
        }
        return result;
    }

    private String getOrderBy(String sort) {
        return (sort == null || sort.isEmpty()) ? "ORDER BY e.id DESC" : matchSort(sort);
    }

    // todo: implement when needed
    private String matchSort(String sort) {
        return "ORDER BY e.id DESC";
    }

    private int getMaxResults(Integer limit) {
        return (limit == null || limit < 0) ? Integer.MAX_VALUE : limit;
    }

    private Object getId(Integer id, String login) {
        return (id == ID_OF_SUMMARY_WALLET) ? login : id;
    }

    private String getWhereClause(Integer id) {
        return (id == ID_OF_SUMMARY_WALLET) ? "WHERE u.login = :id " : "WHERE w.id = :id ";
    }

    @Override
    public void addToUser(Wallet newInstance, User user) {
        postgres.executeQuery(session -> {
            user.getWallets().add(newInstance);
            userDao.update(user);
            return user;
        });
    }

    @Override
    public List<Wallet> getAllFromUser(User user) throws CustomException {
        final List<Wallet> wallets = user.getWallets();
        wallets.sort(comparingInt(Wallet::getId));
        return wallets;
    }

    @Override
    public BigDecimal getSummaryAmountForUser(User user) {
        BigDecimal sum = postgres.executeQuery(session -> {
            String query =
                    "SELECT sum(w.amount) FROM User u " +
                            "JOIN u.wallets w " +
                            "WHERE u.login = :login";
            return session
                    .createQuery(query, BigDecimal.class)
                    .setParameter("login" , user.getLogin())
                    .getSingleResult();
        });
        return Optional.ofNullable(sum).orElse(BigDecimal.ZERO);
    }
}
