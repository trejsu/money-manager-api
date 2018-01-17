package com.money.manager.model;

import com.money.manager.model.money.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name = "WALLET")
public class Wallet {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Embedded
    private Money amount;

    @Column(name = "name")
    private String name;

    @Column(name = "expenses")
    @ElementCollection(fetch = EAGER)
    private List<Expense> expenses;

    @Builder
    public Wallet(Integer id, Money amount, String name, List<Expense> expenses) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.expenses = ofNullable(expenses).orElse(new ArrayList<>());
    }
}
