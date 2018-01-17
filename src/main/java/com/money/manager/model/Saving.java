package com.money.manager.model;

import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "SAVING")
public class Saving implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "icon")
    private byte[] icon;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total")),
            @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    })
    private Money total;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "current")),
            @AttributeOverride(name = "currency", column = @Column(name = "current_currency"))
    })
    private Money current;

    @Column(name = "start_date")
    private LocalDate start;
}
