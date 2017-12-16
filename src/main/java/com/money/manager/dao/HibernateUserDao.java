package com.money.manager.dao;

import com.money.manager.dto.TimePeriod;
import com.money.manager.model.Budget;
import com.money.manager.model.User;
import com.money.manager.model.Wallet;
import com.money.manager.exception.BadRequestException;
import com.money.manager.exception.CustomException;
import com.money.manager.exception.LoginAlreadyTakenException;
import com.money.manager.exception.UserNotFoundException;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.money.manager.util.HibernateUtil.executeQuery;
import static java.util.Comparator.comparingInt;

public class HibernateUserDao implements UserDao {

    @Override
    public String add(User newInstance) throws CustomException {
        checkLoginAvailability(newInstance);
        return executeQuery(session -> (String) session.save(newInstance));
    }

    private void checkLoginAvailability(User newInstance) throws CustomException {
        String login = newInstance.getLogin();
        if (get(login).isPresent()) {
            throw new LoginAlreadyTakenException(
                    "User with login " + login + " already exists!");
        }
    }

    @Override
    public Optional<User> get(String id) throws CustomException {
        return Optional.ofNullable(executeQuery(
                session -> session.get(User.class, id)
        ));
    }

    @Override
    public void update(User transientObject) throws CustomException {
        executeQuery(session -> {
            session.update(transientObject);
            return transientObject;
        });
    }

    @Override
    public List<User> findAll() throws CustomException {
        return executeQuery(session -> session
                .createQuery("FROM User", User.class)
                .list());
    }

    @Override
    public <T> void updateField(String login, String field, T value) throws CustomException {
        User user = get(login).orElseThrow(() -> buildUserNotFoundException(login));
        try {
            PropertyUtils.setProperty(user, field, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BadRequestException("Requested field cannot be updated. Validate entered data and try again.");
        }
        update(user);
    }

    private UserNotFoundException buildUserNotFoundException(String login) {
        return new UserNotFoundException("User with login " + login + " does not exists");
    }
}
