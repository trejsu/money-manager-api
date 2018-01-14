package com.money.manager.auth.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationData {
    @NotNull
    private String login;
    private char[] password;
    private String token;
}
