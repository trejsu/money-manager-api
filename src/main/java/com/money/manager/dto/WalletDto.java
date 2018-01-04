package com.money.manager.dto;

import com.money.manager.model.Wallet;
import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WalletDto {
    private Integer id;
    private Money money;
    private String name;

    public static WalletDto fromWallet(Wallet wallet) {
        return builder()
                .id(wallet.getId())
                .money(new Money(wallet.getAmount(), wallet.getCurrency()))
                .name(wallet.getName())
                .build();
    }

    public Wallet toWallet() {
        return Wallet.builder()
                .id(id)
                .amount(money.getAmount())
                .currency(money.getCurrency().getCurrencyCode())
                .name(name)
                .build();
    }
}
