package com.money.manager.model.money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

import static java.util.Optional.ofNullable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class Money implements Comparable<Money>, Serializable {

    private final static String DEFAULT_CURRENCY = "PLN";

    public final static Money ZERO = new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private Currency currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currencyCode) {
        this.amount = amount;
        this.currency = Currency.getInstance(ofNullable(currencyCode).orElse(DEFAULT_CURRENCY));
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public int compareTo(Money other) {
        Money converted = other.convertTo(currency);
        return amount.compareTo(converted.amount);
    }

    public Money add(Money other) {
        Money converted = other.convertTo(currency);
        BigDecimal newAmount = amount.add(converted.amount);
        return new Money(newAmount, currency);
    }

    public Money substract(Money other) {
        Money converted = other.convertTo(currency);
        BigDecimal newAmount = amount.subtract(converted.amount);
        return new Money(newAmount, currency);
    }

    private Money convertTo(Currency newCurrency) {
        return currency.equals(newCurrency) ? this : new Money(MoneyConverter.convert(amount, currency, newCurrency), newCurrency);
    }
}


