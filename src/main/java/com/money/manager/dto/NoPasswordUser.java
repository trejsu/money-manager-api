package com.money.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoPasswordUser {
    private String login;
    private String firstName;
    private String lastName;
    private boolean admin;
}
