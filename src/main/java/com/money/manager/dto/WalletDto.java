package com.money.manager.dto;

import com.money.manager.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WalletDto {
    private Integer id;
    private BigDecimal amount;
    private String name;

    public static WalletDto fromWallet(Wallet wallet) {
        return builder()
                .id(wallet.getId())
                .amount(wallet.getAmount())
                .name(wallet.getName())
                .build();
    }
}
