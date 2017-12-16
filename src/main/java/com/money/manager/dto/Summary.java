package com.money.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Summary {
    private BigDecimal inflow;
    private BigDecimal outflow;
}
