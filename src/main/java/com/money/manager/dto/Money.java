package com.money.manager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

import static java.util.Optional.ofNullable;

@Value
@AllArgsConstructor
public class Money implements Comparable<Money> {

    private final static String DEFAULT_CURRENCY = "PLN";

    private BigDecimal amount;

    @JsonIgnore
    private Currency currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currencyCode) {
        this.amount = amount;
        System.out.println("amount = " + amount);
        System.out.println("precision = " + amount.precision());
        System.out.println("scale = " + amount.scale());
        final String code = ofNullable(currencyCode).orElse(DEFAULT_CURRENCY);
        this.currency = Currency.getInstance(code);
    }

    @Override
    public int compareTo(Money other) {
        return 0;
    }


    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Money)) return false;
        final Money other = (Money) o;
        final Object this$amount = this.getAmount();
        final Object other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) return false;
        final Object this$currency = this.getCurrency();
        final Object other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $amount = this.getAmount();
        result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
        final Object $currency = this.getCurrency();
        result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
        return result;
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
