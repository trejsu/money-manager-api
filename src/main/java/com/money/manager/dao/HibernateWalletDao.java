package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;
import com.money.manager.model.Expense;
import com.money.manager.model.Wallet;
import com.money.manager.exception.CustomException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
public class HibernateWalletDao implements WalletDao {

    private final static int ID_OF_SUMMARY_WALLET = 0;
    private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;

    private final BudgetDao budgetDao;
    private final UserDao userDao;
    private final ExpenseDao expenseDao;

    @Autowired
    public HibernateWalletDao(BudgetDao budgetDao, UserDao userDao, ExpenseDao expenseDao) {
        this.budgetDao = budgetDao;
        this.userDao = userDao;
        this.expenseDao = expenseDao;
    }

    @Override
    public Integer add(Wallet newInstance) throws CustomException {
        return executeQuery(session -> (Integer) session.save(newInstance));
    }

    @Override
    public Optional<Wallet> get(Integer id) throws CustomException {
        return Optional.of(executeQuery(session -> session.get(Wallet.class, id)));
    }

    @Override
    public void update(Wallet transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public void delete(Wallet persistentObject) throws CustomException {
        executeQuery(session -> {
            session.delete(persistentObject);
            return persistentObject;
        });
    }

    @Override
    public List<Wallet> findAll() throws CustomException {
        return executeQuery(session ->
                        session
                                .createQuery("FROM Wallet", Wallet.class)
                                .list());
    }

    @Override
    public void addExpense(String login, Integer id, Expense expense) throws CustomException {
        executeQuery(session -> {
            expense.setDate(getToday());
            // todo: fix atomicity
            expenseDao.add(expense);
            Wallet wallet = session.get(Wallet.class, id);
            wallet.getExpenses().add(expense);
            updateAmount(wallet, expense);
            return session.save(wallet);
        });
        updateBudgets(login, expense);
    }

    // todo: it fits more to expenses dao
    // todo: user not found
    // todo: wallet not found
    @Override
    public List<Expense> getExpensesByWalletAndTimePeriod(
            String login,
            Integer id,
            TimePeriod timePeriod,
            Integer limit,
            String sort
    ) throws CustomException {
        return executeQuery(session -> {
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
    public Expense getHighestExpenseByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException {
        return executeQuery(session -> {
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
    public Map<String, BigDecimal> getCountedCategoriesByWalletAndTimePeriod(String login, Integer id, TimePeriod timePeriod) throws CustomException {
        return executeQuery(session -> {
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

    private void updateBudgets(String login, Expense expense) throws CustomException {
        List<Budget> budgets = userDao.getBudgetsByLoginAndTimePeriod(login, new TimePeriod(null, getToday()), new TimePeriod(getToday(), null));
        for (Budget budget : budgets) {
            if (budget.getCategory().getName().equals(expense.getCategory().getName())) {
                budget.setCurrent(budget.getCurrent().add(expense.getAmount()));
                budgetDao.update(budget);
            }
        }
    }

    private String getToday() {
        return LocalDate.now().format(DATE_TIME_FORMATTER);
    }

    private void updateAmount(Wallet wallet, Expense expense) {
        BigDecimal amount = expense.getAmount();
        if (!expense.getCategory().isProfit()) {
            amount = amount.negate();
        }
        wallet.setAmount(wallet.getAmount().add(amount));
    }
}
