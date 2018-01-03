package com.money.manager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

import static java.util.Optional.ofNullable;

@Value
public class Money {

    private BigDecimal amount;

    @JsonIgnore
    private Currency currency;

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currency") String currencyCode) {
        this.amount = amount;
        this.currency = Currency.getInstance(ofNullable(currencyCode).orElse("PLN"));
    }
}
