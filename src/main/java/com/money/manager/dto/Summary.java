package com.money.manager.dto;

import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Summary {
    private Money inflow;
    private Money outflow;
}
