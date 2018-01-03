package com.money.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Summary {
    private Money inflow;
    private Money outflow;
}
