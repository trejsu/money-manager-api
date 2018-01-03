package com.money.manager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedList;
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

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "name")
    private String name;

    @Column(name = "expenses")
    @ElementCollection(fetch = EAGER)
    private List<Expense> expenses;

    @Builder
    public Wallet(Integer id, BigDecimal amount, String name, List<Expense> expenses) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.expenses = ofNullable(expenses).orElse(new LinkedList<>());
    }
}
