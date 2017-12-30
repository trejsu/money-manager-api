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

    private final PostgresUtil postgres;

    @Autowired
    public PostgresWalletDao(PostgresUtil postgres) {
        this.postgres = postgres;
    }

    @Override
    public Integer add(Wallet newInstance) {
        return postgres.executeQuery(session -> (Integer) session.save(newInstance));
    }

    @Override
    public void update(Wallet transientObject) {
        postgres.executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
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
        while (itr.hasNext()) {
            Object[] obj = (Object[]) itr.next();
            result.put((String) obj[0], (BigDecimal) obj[1]);
        }
        return result;
    }

    private Object getId(Integer id, String login) {
        return (id == ID_OF_SUMMARY_WALLET) ? login : id;
    }

    private String getWhereClause(Integer id) {
        return (id == ID_OF_SUMMARY_WALLET) ? "WHERE u.login = :id " : "WHERE w.id = :id ";
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
