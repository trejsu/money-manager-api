package com.money.manager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

import static java.util.Optional.ofNullable;

@Value
@EqualsAndHashCode
public class Money implements Comparable<Money> {

    private final static String DEFAULT_CURRENCY = "PLN";

    private BigDecimal amount;

    @JsonIgnore
    private Currency currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currencyCode) {
        this.amount = amount;
        final String code = ofNullable(currencyCode).orElse(DEFAULT_CURRENCY);
        this.currency = Currency.getInstance(code);
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public int compareTo(Money other) {
        return 0;
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    public Money add(Money other) {
        Money converted = other.convertTo(currency);
        BigDecimal newAmount = amount.add(converted.amount);
        return new Money(newAmount, currency);
    }

    // todo: move it from here
    private final static HashMap<String, BigDecimal> CONVERSION_RATES = new HashMap<>();
    static { CONVERSION_RATES.put(DEFAULT_CURRENCY, BigDecimal.ONE); }

    private BigDecimal getRate(Currency from, Currency to) {
        final BigDecimal fromRate = CONVERSION_RATES.get(from.getCurrencyCode());
        final BigDecimal toRate = CONVERSION_RATES.get(to.getCurrencyCode());
        return fromRate.multiply(BigDecimal.ONE.divide(toRate, BigDecimal.ROUND_HALF_UP));
    }

    private Money convertTo(Currency newCurrency) {
        if (currency.equals(newCurrency)) {
            return this;
        } else {
            final BigDecimal newAmount = amount.multiply(getRate(currency, newCurrency));
            return new Money(newAmount, newCurrency);
        }
    }
}
