package com.money.manager.model.money;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

class MoneyConverter {

    private final static String BASE_CURRENCY = "PLN";
    private final static HashMap<String, BigDecimal> CONVERSION_RATES = new HashMap<>();

    static {
        CONVERSION_RATES.put(BASE_CURRENCY, BigDecimal.ONE);
    }

    static BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        return amount.multiply(getRate(from, to));
    }

    private static BigDecimal getRate(Currency from, Currency to) {
        final BigDecimal fromRate = CONVERSION_RATES.get(from.getCurrencyCode());
        final BigDecimal toRate = CONVERSION_RATES.get(to.getCurrencyCode());
        return fromRate.multiply(BigDecimal.ONE.divide(toRate, BigDecimal.ROUND_HALF_UP));
    }
}
