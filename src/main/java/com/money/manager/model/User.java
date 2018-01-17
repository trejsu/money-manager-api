package com.money.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;


@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ACCOUNT")
public class User {
    @Id
    @Column(name = "login")
    private String login;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private char[] password;

    @Column(name = "admin")
    private boolean admin;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "wallets")
    private List<Wallet> wallets;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "budgets")
    private List<Budget> budgets;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "savings")
    private List<Saving> savings;

    @Builder
    public User(String login, String firstName, String lastName, char[] password, boolean admin, List<Wallet> wallets,
                List<Budget> budgets, List<Saving> savings) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.admin = admin;
        this.wallets = ofNullable(wallets).orElse(new ArrayList<>());
        this.budgets = ofNullable(budgets).orElse(new ArrayList<>());
        this.savings = ofNullable(savings).orElse(new ArrayList<>());
    }
}
