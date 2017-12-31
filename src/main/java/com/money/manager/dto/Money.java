package com.money.manager.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value
public class Money {

    private BigDecimal amount;
    private Currency currency;

}
