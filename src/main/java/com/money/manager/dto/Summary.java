package com.money.manager.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Summary {
    private BigDecimal inflow;
    private BigDecimal outflow;
}
