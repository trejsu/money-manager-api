package com.money.manager.dto;

import com.money.manager.model.Wallet;
import com.money.manager.model.money.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WalletDto {

    private Integer id;

    @NotNull
    @Valid
    private Money amount;

    @NotNull
    private String name;

    public static WalletDto fromWallet(Wallet wallet) {
        return builder()
                .id(wallet.getId())
                .amount(wallet.getAmount())
                .name(wallet.getName())
                .build();
    }

    public Wallet toWallet() {
        return Wallet.builder()
                .id(id)
                .amount(amount)
                .name(name)
                .build();
    }
}
